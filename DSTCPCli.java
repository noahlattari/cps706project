import java.net.*;
import java.io.*;

public class DSTCPCli {
    private Socket clientSocket;
    private PrinterWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() {
        in.close();
        out.close();
        clientSocket.close();
    }
}

@Test
public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
    DSTCPCli client = new DSTCPCli();
    client.startConnection("127.0.0.1", 20650);
    String response = client.sendMessage("hello server");
    assertEquals("hello client", response);
}