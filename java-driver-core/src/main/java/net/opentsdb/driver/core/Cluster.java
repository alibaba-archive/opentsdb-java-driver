package net.opentsdb.driver.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.opentsdb.driver.core.conf.Configuration;

/**
 * We got a Session which contains the single http client 
 * */
public class Cluster implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(Cluster.class);
    private final String host;
    private final int port;
    private final Configuration configuration;
    private BlockingQueue<Session> sessions = new LinkedBlockingQueue<>();
    private Session masterSession;

    /**
     * Constructs a new Cluster instance.
     * <p/>
     * This constructor is mainly exposed so Cluster can be sub-classed as a means to make
     * testing/mocking easier or to "intercept" its method call. Most users shouldn't extend this
     * class however and should prefer using the {@link #builder}.
     *
     * @param host the host to contact to the cluster.
     * @param port the port to contact to the new cluster.
     * @param configuration the configuration for the new cluster.
     */
    public Cluster(String host, int port, Configuration configuration) {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        this.masterSession = new Session(host, port, configuration);
    }

    public Cluster(Builder builder) {
        this(builder.getHost(), builder.getPort(), builder.getConfiguration());
    }

    /**
     * Creates a new {@link Cluster.Builder} instance.
     * <p/>
     *
     * @return the new cluster builder.
     */
    public static Cluster.Builder builder() {
        return new Cluster.Builder();
    }

    /**
     * Helper class to build {@link Cluster} instances.
     */
    public static class Builder {
        private String host;
        private int port = TConstants.DEFAULT_PORT;
        private Configuration configuration;


        /**
         * Set the configuration of the cluster ;
         */

        public Builder withConfiguration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * Set the address of the cluster; the address is use to connection to remote cluster of
         * opentsdb.
         * 
         * @param host the String of host
         * @return this Builder
         */
        public Builder withAddress(String host) {
            this.host = host;
            return this;
        }

        /**
         * Set the port of the opentsdb cluster; when the port is not set then the deafult port of
         * Tconsts.DEFAULT_PORT is used.
         * 
         * @param port
         * @return this Builder
         */
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public Cluster build() {
            return new Cluster(this);
        }
    }

    /**
     * The connect method for this cluster, and it can just start the http async client ,then return
     * the session.
     * 
     * @return session the session for latter operation
     */
    public Session connect() throws IOReactorException {
        if (this.masterSession != null) {
            this.masterSession.start();
            LOG.info("The cluster have been successfully started .");
            return this.masterSession;
        } else {
            return masterSession;
        }
    }
    
    /**
     * Create new Session which 
     * @throws IOReactorException 
     * */
    public Session newSession() throws IOReactorException {
        Session session = new Session(host, port, configuration);
        session.start();
        sessions.add(session);
        return session;
    }
    
    /**
     * Close the sepcified session
     * 
     * @param session the session to close
     * @throws IOException 
     * */
    public void close(Session session) throws IOException {
        if (session != null) {
            session.close();
            sessions.remove(session);
        }
    }

    /**
     * return all new sessions
     * */
    public BlockingQueue<Session> getSessions(){
        return sessions;
    }
    
    /**
     * close all session
     * */
    @Override
    public void close() throws IOException {
        if (this.masterSession != null) {
            this.masterSession.close();
            this.masterSession = null;
        }
        if (sessions.size() != 0) {
            for (Session session : sessions) {
                session.close();
            }
        }
        sessions.clear();
        LOG.info("The cluster have been successfully closed .");
    }
}
