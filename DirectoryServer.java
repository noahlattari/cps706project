import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class TCPServer
{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWrite out;
    private BufferedReader in;
    private String ipList;
    private String clientID;
    private DirectoryServer directoryServer;


    public TCPServer(int port, DirectoryServer ds)
    {
        this.directoryServer = ds;
        this.serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
    }

    public void start()
    {
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

//    public static void main(String[] args)
//    {
//        DSTCPSrv server = new DSTCPSrv(20650);
//        server.start();
//    }
}

public class TCPClient {
    private Socket clientSocket;
    private PrinterWriter out;
    private BufferedReader in;
    private String targetID;
    private String ipList;
    private DirectoryServer directoryServer;

    public TCPClient(String ip, int port, DirectoryServer ds)
    {
        this.targetID = targetID;
        this.ipList = ipList;
        clientSocket = new Socket(ip, port);
        this.directoryServer = ds;
    }

    public void start(String ip, int port) {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stop() {
        in.close();
        out.close();
        clientSocket.close();
    }
}

//    @Test
//    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
//        DSTCPCli client = new DSTCPCli();
//        client.startConnection("127.0.0.1", 20650);
//        String response = client.sendMessage("hello server");
//        assertEquals("hello client", response);
//    }

class UDPServer extends Thread
{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];
    DirectoryServer directoryServer;


    public UDPServer(int port, DirectoryServer ds);
    {
        buffer = new byte[256];
        socket = new DatagramSocket(port);
        directoryServer = ds;
        running = true;
    }

    public void run()
    {
        while(running)
        {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            int port = packet.getPort();
            InetAddress address = packet.getAddress();

            packet = new DatagramPacket(buffer, buffer.length, address, port);
            String msg = new String(packet.getData(), 0, packet.getLength());

            if (msg.equals("end"))
            {
                running = false;
                continue;
            }
            socket.send(packet);
        }
        socket.close();
    }

    public void stop()
    {
        socket.close();
    }
}

public class DirectoryServer {
    private String ip;
    private int id;
    private int port;
    private Map<String, List<String>> data;
    private String prev;
    private String next;
    private String primaryDS;

    private UDPServer udpServer;
    private TCPClient tcpClient;
    private TCPServer tcpServer;

    private String ipList;

    public DirectoryServer(int id, int port) {
        this.ip = InetAddress.getLocalHost().getHostAddress();
        this.id = id;
        this.port = port;

        this.data = new HashMap<String, List<String>>();

        startTCPServer();
        startUDPServer();
    }

    public void startUDPServer() {
        udpServer = new UDPServer(port, this);
        udpServer.start();
    }

    /** TCPServer should start TCP Client to send **/
    public void startTCPServer() {
        TCPServer tcpServer = new TCPServer(port, this);
        tcpServer.start();
    }

    /** Only used for Node #1 instantiated by TCPServer **/
    public String getIPS() {
        tcpClient = new TCPClient(ip, port, this);
        tcpClient.start();
        // TCPServer will set the value of ipList
    }

    public void setIPS(String msg) {
        ipList = msg;
    }

    public void setPrimary(String primaryNode) {
        primaryDS = primaryNode;
    }

    public void setNeighbors(String prev, String next) {
        this.prev = prev;
        this.next = next;
    }

    public String getPrimaryDS() {
        return primaryDS;
    }

    public List<String> getNeighbors {
        List<String> neighbors = new ArrayList<String>();
        neighbors.add(this.prev);
        neighbors.add(this.next);

        return neighbors;
    }

    public String getPort() {
        return port;
    }

    public String getIP() {
        return ip;
    }

    public String getPrimaryDS() {
        return primaryDS;
    }

    public void setData(String contentName, String location) {
        List<String> storedValues = data.get(contentName);

        if(storedValues != null) {
            storedValues.add(location);
        }
        else {
            storedValues = new ArrayList<String>();
            storedValues.add(location);
        }

        data.put(contentName, storedValues);
    }

    public List<String> getData(String contentName) {
        return data.get(contentName);
        /** Add NULL Check **/
    }

    public boolean removeData(String contentName, String location) {
        List<String> storedValues = data.get(contentName);

        if(storedValues != null && storedValues.contains(location)) {
            storedValues.remove(location);
            data.put(contentName, storedValues);
            return true;
        }

        return false;
    }
}