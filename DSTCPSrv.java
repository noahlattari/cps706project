import java.net.*;
import java.io.*;

public class DSTCPSrv
{
    private Serversocket serverSocket;
    private Socket clientSocket;
    private PrintWrite out;
    private BufferedReader in;

    public void start(int port)
    {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamreader(clientSocket.getInputStream()));
        String greeting = in.readLine();

        if ("hello server".equals(greeting))
        {
            out.println("hello client");
        }
        else
        {
            out.println("unrecognized greeting");
        }

    }

    public void stop()
    {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args)
    {
        DSTCPSrv server = new DSTCPSrv();
        server.start(20650);
    }

}