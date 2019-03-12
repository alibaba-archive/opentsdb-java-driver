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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.opentsdb.driver.core.conf.Configuration;

public class TestClusterStress {
    private static final Logger LOG = LoggerFactory.getLogger(TestClusterStress.class);

    private ExecutorService executorService;

    @Before
    public void init() {
        // 8 threads should be enough so that we stress the driver and not the OS thread scheduler
        executorService = Executors.newFixedThreadPool(8);
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
    
    @Test
    public void testClusterNoLeakConnection() throws IOException {
        int numberOfClusters = 10;
        int numberOfIterations = 1000;
        try {
            //start 500 time to create cluster and close;
            for (int i = 1; i < numberOfIterations; i++) {
                LOG.info("On iteration {}/{}.", i, numberOfIterations);
                LOG.info("Creating {} clusters", numberOfClusters);
                List<CreateClusterAndCheckConnections> actions =
                        waitForCreates(createClustersConcurrently(numberOfClusters));
                waitForCloses(closeClustersConcurrently(actions));
                if (LOG.isDebugEnabled())
                    LOG.debug("# {} threads currently running",
                            Thread.getAllStackTraces().keySet().size());
            }
        } finally {
            LOG.info("finish test");
        }

    }

    // wait for connection to create
    private List<Future<CreateClusterAndCheckConnections>> createClustersConcurrently(
            int numberOfClusters) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        return createClustersConcurrently(numberOfClusters, countDownLatch);
    }

    private List<Future<CreateClusterAndCheckConnections>> createClustersConcurrently(
            int numberOfClusters, CountDownLatch countDownLatch) {
        List<Future<CreateClusterAndCheckConnections>> clusterFutures =
                new ArrayList<>(numberOfClusters);
        for (int i = 0; i < numberOfClusters; i++) {
            clusterFutures.add(
                    executorService.submit(new CreateClusterAndCheckConnections(countDownLatch)));
        }
        countDownLatch.countDown();
        return clusterFutures;
    }

    //wait for connection to close
    private List<Future<Void>> closeClustersConcurrently(
            List<CreateClusterAndCheckConnections> actions) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        return closeClustersConcurrently(actions, countDownLatch);
    }

    private List<Future<Void>> closeClustersConcurrently(
            List<CreateClusterAndCheckConnections> actions, CountDownLatch startSignal) {
        List<Future<Void>> closeFutures = new ArrayList<>(actions.size());
        for (CreateClusterAndCheckConnections action : actions) {
            closeFutures.add(executorService
                    .submit(new CloseCluster(action.cluster,  startSignal)));
        }
        startSignal.countDown();
        return closeFutures;
    }

    private List<CreateClusterAndCheckConnections> waitForCreates(
            List<Future<CreateClusterAndCheckConnections>> futures) throws IOException {
        List<CreateClusterAndCheckConnections> actions =
                new ArrayList<>(futures.size());
        // If an error occurs, we will abort the test, but we still want to close all the clusters
        // that were opened successfully, so we iterate over the whole list no matter what.
        AssertionError error = null;
        for (Future<CreateClusterAndCheckConnections> future : futures) {
            try {
                actions.add(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (error == null)
                    error = assertionError("Interrupted while waiting for future creation", e);
            } catch (ExecutionException e) {
                if (error == null) {
                    Throwable cause = e.getCause();
                    if (cause instanceof AssertionError)
                        error = (AssertionError) cause;
                    else
                        error = assertionError("Error while creating a cluster", cause);
                }
            }
        }
        if (error != null) {
            for (CreateClusterAndCheckConnections action : actions)
                action.cluster.close();
            throw error;
        } else
            return actions;
    }

    private List<Void> waitForCloses(List<Future<Void>> futures) {
        List<Void> result = new ArrayList<Void>(futures.size());
        AssertionError error = null;
        for (Future<Void> future : futures) {
            try {
                result.add(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (error == null)
                    error = assertionError("Interrupted while waiting for future", e);
            } catch (ExecutionException e) {
                if (error == null) {
                    Throwable cause = e.getCause();
                    if (cause instanceof AssertionError)
                        error = (AssertionError) cause;
                    else
                        error = assertionError("Error while closing a cluster", cause);
                }
            }
        }
        if (error != null)
            throw error;
        else
            return result;
    }

    private class CreateClusterAndCheckConnections
            implements Callable<CreateClusterAndCheckConnections> {
        private final CountDownLatch signal;
        private Cluster cluster;
        private Configuration configuration = new Configuration.Builder().build();

        CreateClusterAndCheckConnections(CountDownLatch signal) {
            this.signal = signal;
            this.cluster = new Cluster.Builder().withAddress("locahhost").withPort(4242)
                    .withConfiguration(configuration).build();
        }

        @Override
        public CreateClusterAndCheckConnections call() throws Exception {
            signal.await();
            try {
                // There should be 1 control connection after initializing.
                cluster.connect();
                return this;
            } catch (AssertionError e) {
                // If an assertion fails, close the cluster now, because it's the last time we
                // have a reference to it.
                cluster.close();
                cluster = null;
                throw e;
            } finally {
            }
        }
    }

    private class CloseCluster implements Callable<Void> {
        private Cluster cluster;
        private final CountDownLatch signal;

        CloseCluster(Cluster cluster, CountDownLatch signal) {
            this.cluster = cluster;
            this.signal = signal;
        }

        @Override
        public Void call() throws Exception {
            signal.await();
            try {
                cluster.close();
            } finally {
                cluster = null;
            }
            return null;
        }
    }

    private static AssertionError assertionError(String message, Throwable cause) {
        AssertionError error = new AssertionError(message);
        error.initCause(cause);
        return error;
    }

}
