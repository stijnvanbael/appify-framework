package be.appify.framework.test.service;

import org.eclipse.jetty.server.Server;
import org.junit.*;

public class WebServiceClientTest {
    private static Server server;
    private static TestHandler handler;
    protected static final int PORT = 50599;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new Server(PORT);
        handler = new TestHandler();
        server.setHandler(handler);
        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
    }

    protected static void addStubResponse(String requestURIRegex, String responseResourcePath) {
        handler.addStubResponse(requestURIRegex, responseResourcePath);
    }
}