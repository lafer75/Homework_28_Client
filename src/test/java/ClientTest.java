

import org.example.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

public class ClientTest {

    private static final String HOST = "localhost";
    private static final int PORT = 8088;
    private static final String NAME = "client1";

    private ByteArrayOutputStream outContent;
    private ByteArrayInputStream inContent;

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testConnectWithInvalidHost() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Client.connect("", PORT));
    }

    @Test
    public void testConnectWithInvalidPort() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Client.connect(HOST, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Client.connect(HOST, 65536));
    }

    @Test
    public void testIncomingMessageHandler() throws IOException {
        inContent = new ByteArrayInputStream("Hello World\n".getBytes());
        System.setIn(inContent);

        Client.IncomingMessageHandler handler = new Client.IncomingMessageHandler();
        Thread thread = new Thread(handler);
        thread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals("Hello World", outContent.toString().trim());
    }

}
