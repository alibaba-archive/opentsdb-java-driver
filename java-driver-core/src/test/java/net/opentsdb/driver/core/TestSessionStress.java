/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.opentsdb.driver.core;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import jdk.nashorn.internal.ir.Block;
import net.opentsdb.driver.core.conf.Configuration;

public class TestSessionStress {
    private static final Logger LOG = LoggerFactory.getLogger(TestSessionStress.class);

    private ListeningExecutorService executorService;

    private Cluster cluster;

    @Before
    public void init() {
        // 8 threads should be enough so that we stress the driver and not the OS thread scheduler
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8));
    }

    @After
    public void shutdown() throws Exception {
        executorService.shutdown();
        try {
            boolean shutdown = executorService.awaitTermination(30, TimeUnit.SECONDS);
            if (!shutdown)
                throw new Exception("executor ran for longer than expected");
        } catch (InterruptedException e) {
            throw new Exception("Interrupted while waiting for executor to shutdown");
        } finally {
            executorService = null;
            System.gc();
        }
    }

    /**
     * Stress test on operate sessions.
     * <p/>
     * This test operate {@code Session} in a multithreaded environment and makes sure that there is
     * not connection leak. More specifically, this test performs the following steps:
     * <p/>
     * <ul>
     * <li>Open and close {@code Session} 1000 times</li>
     * <li>verify single {@code Session} is closed</li>
     * <li>Open 1000 new Session concurrently</li>
     * <li>Verify that 1000 sessions are reported as open by the {@code Cluster}</li>
     * <li>Close 500 {@code Session} concurrently</li>
     * <li>Verify that 500 sessions are reported as open by the {@code Cluster}</li>
     * </ul>
     * <p/>
     * 
     * @throws Exception
     */
    @Test
    public void testSessionsLeak() throws Exception {
        // override inherited field with a new cluster object and ensure 0 sessions and connections
        Configuration configuration = new Configuration.Builder().build();
        cluster = Cluster.builder().withAddress("localhost").withPort(4242)
                .withConfiguration(configuration).build();

        try {
            // first get on session
            Session session = cluster.connect();
            for (int i = 0; i != 1000; ++i) {
                session.close();
                assertTrue(!session.isRunning());
                session.start();
                assertTrue(session.isRunning());
            }
            session.close();
            // Closing the session keeps the control connection opened
            int nbOfSessions = 100;
            int nbOfIterations = 5;

            for (int iteration = 1; iteration <= nbOfIterations; iteration++) {
                System.out.println("On iteration " + iteration + "/" + nbOfIterations );
                LOG.info("On iteration {}/{}.", iteration, nbOfIterations);
                LOG.info("Creating {} sessions.", nbOfSessions);
                System.out.println("Creating "+ iteration * nbOfSessions +" sessions.");
                waitFor(openSessionsConcurrently(nbOfSessions));

                // Close half of the sessions asynchronously
                LOG.info("Closing {} sessions.", nbOfSessions);
                System.out.println("Closing " + iteration * nbOfSessions + " sessions ");
                waitFor(closeSessionsConcurrently(nbOfSessions));
            }
            
            System.out.println("finished sessions");
            BlockingQueue<Session> sessions = cluster.getSessions();
            int i = 0;
            for (Session session2 : sessions) {
                if (session2.isRunning()) {
                    ++i;
                }
            }
            assertTrue(i == 0);
        } finally {
            cluster.close();
            cluster = null;
        }
    }

    // open session concurrent ,wait for the session to open
    private List<ListenableFuture<Session>> openSessionsConcurrently(int iterations) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        return openSessionsConcurrently(iterations, countDownLatch);
    }

    //concurrent open the new session
    private List<ListenableFuture<Session>> openSessionsConcurrently(int iterations,
            CountDownLatch countDownLatch) {
        // Open new sessions once all tasks have been created
        List<ListenableFuture<Session>> sessionFutures = new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            sessionFutures.add(executorService.submit(new OpenSession(countDownLatch)));
        }
        countDownLatch.countDown();
        return sessionFutures;
    }

    //concurrent close the session
    private List<ListenableFuture<Void>> closeSessionsConcurrently(int iterations) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        return closeSessionsConcurrently(iterations, countDownLatch);
    }
    
    //real close session operation
    private List<ListenableFuture<Void>> closeSessionsConcurrently(int iterations,
            CountDownLatch countDownLatch) {
        // Get a reference to every session we want to close
        Stack<Session> sessionsToClose = new Stack<Session>();
        Iterator<? extends Session> iterator = cluster.getSessions().iterator();
        for (int i = 0; i < iterations; i++) {
            sessionsToClose.push(iterator.next());
        }

        List<ListenableFuture<Void>> closeFutures = new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            closeFutures.add(executorService
                    .submit(new CloseSession(cluster, sessionsToClose.pop(), countDownLatch)));
        }
        countDownLatch.countDown();

        // Immediately wait for CloseFutures, this should be very quick since all this work does is
        // call closeAsync.
        return closeFutures;
    }

    private <E> void waitFor(List<ListenableFuture<E>> futures) throws Exception {
        for (Future<E> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while waiting for future", e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }
        }
    }

    private class OpenSession implements Callable<Session> {
        private final CountDownLatch signal;

        OpenSession(CountDownLatch signal) {
            this.signal = signal;
        }

        @Override
        public Session call() throws Exception {
            signal.await();
            return cluster.newSession();
        }
    }

    private static class CloseSession implements Callable<Void> {
        private Session session;
        private final CountDownLatch signal;
        private Cluster cluster;

        CloseSession(Cluster cluster, Session session, CountDownLatch startSignal) {
            this.cluster = cluster;
            this.session = session;
            this.signal = startSignal;
        }

        @Override
        public Void call() {
            try {
                signal.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                try {
                    cluster.close(session);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } ;
            } finally {
                session = null;
            }
            return null;
        }
    }

}
