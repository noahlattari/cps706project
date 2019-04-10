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
    private DirectoryServer directoryServer;


    public TCPServer(int port, DirectoryServer ds)
    {
        this.directoryServer = ds;
        this.serverSocket = new ServerSocket(port);
    }

    public void start()
    {
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamreader(clientSocket.getInputStream()));

        String command = in.readLine();
        if(command.equals("init")) {
            out.println(directoryServer.getNextIP());
        } else {
            out.println("Unknown. Please Retry");
        }
    }

    public void stop()
    {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}

public class TCPClient {
    private DirectoryServer directoryServer;
    private Socket socket;
    private PrinterWriter out;
    private BufferedReader in;

    public TCPClient(DirectoryServer ds)
    {
        directoryServer = ds;
    }

    public void start(String nextIp, int port) {
        socket = new Socket(nextIP, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String sendRequest(String command) {
        out.println(ipList);
        String resp = in.readLine();
        return resp;
    }

    public void stop() {
        in.close();
        out.close();
        socket.close();
    }
}

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
            socket.receive(packet); // receive packet from client

            int port = packet.getPort(); //port of client
            InetAddress address = packet.getAddress(); //address of client

            String command = new String(packet.getData(), 0, packet.getLength()); //get command

            if (command.equals("init")) {
                String ipList = directoryServer.getIPS();
                buffer = ipList.getBytes();
            }

            // Ex. query content_name - Assume format is "query contentName"
            else if (command.contains("query")) {
                String result = directoryServer.getData(command.split(" ")[1]);

                String combined = "":
                for (String value : result) {
                    combined += value;
                    combined += ","; //format is ip1,ip2...
                }

                combined = combined.substring(0, combined.length() - 1);
                buffer = combined.getBytes();
            }

            else if (command.equals("exit")) {
                buffer = ("Exiting . . . ").getBytes();
                running = false;
                continue;

            } else {
                buffer = ("Invalid. Retry").getBytes();
            }

            packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        }
        stop();
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

        tcpClient = new TCPClient(this);

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

    public String getID() {
        return id.toString();
    }

    /** Only used for Node #1 instantiated by TCPServer **/
    public String getIPS() {
        String ipList = this.ip + "," + this.next;
        String neighbor = this.next;

        for (int i = 0; i < 2; i++) {
            tcpClient.start(neighbor, this.port);
            String neighbor = tcpClient.sendRequest("init");
            ipList += "," + neighbor;

        } // Only need two iterations since Node1 already knows Node1 and Node2.
        //   Each call returns it's next neighbor I.E. Node3 returns ip of Node4

        return ipList;
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

    public getNextIP() {
        return next;
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

    public static void main(String args[]) {




    }

}