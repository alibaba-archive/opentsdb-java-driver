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
package net.opentsdb.driver.stress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.opentsdb.driver.core.Cluster;
import net.opentsdb.driver.core.Session;
import net.opentsdb.driver.core.conf.Configuration;
import net.opentsdb.driver.core.exceptions.ErrorValueTypeException;
import net.opentsdb.driver.core.model.DataPoint;

/**
 * A simple stress tool to demonstrate the use of the driver.
 * <p/>
 * Sample usage: stress put n 100000 t 10 stress query n 10000 t 10
 */
public class Stress {
    private static final Logger LOG = LoggerFactory.getLogger(Stress.class);

    private int threadNumber;
    private long count;
    private String operation;
    private String host;
    private int port;
    private int batch;

    private static Options OPTIONS;
    private static HelpFormatter OPTIONS_FORMATTER;


    private static final Random randomSeed = new Random(System.currentTimeMillis());

    private static long nextRandomSeed() {
        return randomSeed.nextLong();
    }

    protected final Random rand = new Random(nextRandomSeed());

    private String[] opStrings = {"query", "put"};

    static {
        OPTIONS = new Options();
        OPTIONS.addOption("t", "threadNumber", true, "thread number to run all stress");
        OPTIONS.addOption("n", "number", true, "query / put data point number ");
        OPTIONS.addOption("o", "operation", true, "query / put data operation ");
        OPTIONS.addOption("h", "host", true, "server host ");
        OPTIONS.addOption("p", "port", true, "server port ");
        OPTIONS.addOption("b", "port", true, "use put multi data point");
        OPTIONS_FORMATTER = new HelpFormatter();
    }

    private static void printUsage() {
        OPTIONS_FORMATTER.printHelp("stress -o query -n 1000 -t 10 -h host -p port -b 5", OPTIONS,
                true);
        System.exit(1);
    }


    private boolean parseOptions(String[] args) throws ParseException {
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Stress", OPTIONS, true);
            return false;
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(OPTIONS, args);
        if (cmd.hasOption('n')) {
            count = Long.parseLong(cmd.getOptionValue('n'));
        }

        if (cmd.hasOption('t')) {
            threadNumber = Integer.valueOf(cmd.getOptionValue('t'));
        }

        if (cmd.hasOption('o')) {
            operation = cmd.getOptionValue('o');
            if (!Arrays.asList(opStrings).contains(operation)) {
                return false;
            }
        }

        if (cmd.hasOption('h')) {
            host = cmd.getOptionValue('h');
        }

        if (cmd.hasOption('p')) {
            port = Integer.parseInt(cmd.getOptionValue('p'));
        }

        if (cmd.hasOption('b')) {
            batch = Integer.parseInt(cmd.getOptionValue('b'));
        }
        return true;
    }

    private int run(String[] args) throws InterruptedException, IOException {
        try {
            if (!parseOptions(args)) {
                return 1;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.error("init parse option error");
        }

        Future<RunResult>[] threads = new Future[threadNumber];
        RunResult[] results = new RunResult[threadNumber];
        ExecutorService pool = Executors.newFixedThreadPool(threadNumber,
                new ThreadFactoryBuilder().setNameFormat("StressClient-%s").build());

        Configuration configuration = new Configuration.Builder().build();
        Cluster cluster = new Cluster.Builder().withAddress(host).withPort(port)
                .withConfiguration(configuration).build();
        Session session;
        try {
            session = cluster.connect();
        } catch (IOReactorException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return 1;
        }

        final long start = System.currentTimeMillis();
        long numberPerThread = count / threadNumber;

        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = pool.submit(new Callable<RunResult>() {
                @Override
                public RunResult call() throws Exception {
                    RunResult run = runStress(session, numberPerThread, operation);
                    LOG.info("Finished " + Thread.currentThread().getName() + " in " + run.duration
                            + "ms over " + count / threadNumber + " rows");
                    return run;
                }
            });
        }
        pool.shutdown();

        for (int i = 0; i < threads.length; i++) {
            try {
                results[i] = threads[i].get();
            } catch (ExecutionException e) {
                throw new IOException(e.getCause());
            }
        }

        if (session != null && cluster != null) {
            cluster.close();
        }
        return 0;
    }

    private RunResult runStress(Session session, long numberPerThread, String operation)
            throws Exception {
        if (operation.equals("put")) {
            int i = 0;
            long startTs = System.currentTimeMillis();
            while (i != numberPerThread) {
                if (batch == 0) {
                    session.put(generateRandomDp());
                    ++i;
                } else {
                    ArrayList<DataPoint> arrayList = new ArrayList<>();
                    for (int c = 0; c != batch; ++c) {
                        arrayList.add(generateRandomDp());
                    }
                    session.put(arrayList, true, true);
                }
            }
            return new RunResult(System.currentTimeMillis() - startTs);
        }
        return null;
    }

    private DataPoint generateRandomDp() throws ErrorValueTypeException {
        Long ts = System.currentTimeMillis();
        HashMap<String, String> tags = new HashMap<>();
        tags.put("tagk1", "tagv1");
        tags.put("tagk2", "tagv2");
        DataPoint dataPoint =
                new DataPoint.Builder().setMetric("metric" + System.nanoTime() + rand.nextInt())
                        .setTimestamp(ts).setTags(tags).setValue(ts).build();
        return dataPoint;
    }

    protected static class RunResult implements Comparable<RunResult> {
        public RunResult(long duration) {
            this.duration = duration;
        }

        public final long duration;

        @Override
        public String toString() {
            return Long.toString(duration);
        }

        @Override
        public int compareTo(RunResult o) {
            return Long.compare(this.duration, o.duration);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Stress stress = new Stress();
        System.exit(stress.run(args));
    }
}
