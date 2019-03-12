package net.opentsdb.driver.core;

import java.io.IOException;
import org.junit.Test;
import net.opentsdb.driver.core.conf.Configuration;

public class TestCluster {
    private final String host = "localhost";
    private final int port = 4242;

    @Test
    public void testCluster() throws IOException {
        Configuration configuration = new Configuration.Builder().build();
        Cluster cluster = new Cluster.Builder().withAddress(host).withPort(port)
                .withConfiguration(configuration).build();
        try {
            cluster.connect();
        }catch (Exception e) {
            // TODO: handle exception
        }finally {
            cluster.close();
        }
    }
}
