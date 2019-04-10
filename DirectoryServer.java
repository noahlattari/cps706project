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
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;

/**
 *  CPS 706 - Socket Programming Project
 *  Directory Server Implementation
 *  Purpose: To implement the Directory Server aspect of Socket Programming
 *
 *  @Author Jonathan Lam, Judel Villardo, Noah Lattari
 *  @Version 1.0
 **/


/**
 * TCPServer - This class is used to establish and handle all Server communciation
 *             between Directory Servers (DS).
 *             Primary usage is for iterating through the DS and return all IPs in DHT
 */
class TCPServer
{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private DirectoryServer directoryServer;


    /**
     * TCPServer - Constructor method for TCPServer
     * @param port - port number of machine
     * @param ds - directory server associated with TCP Server
     */
    public TCPServer(int port, DirectoryServer ds)
    {
        this.directoryServer = ds;

        try{
            this.serverSocket = new ServerSocket(port);
        }
        catch(IOException ex) {
            System.out.println("Could not setup Server Socket.");
        }

    }

    /**
     * start - Awaits for a client to connect with Server and handles inputs based on message
     */
    public void start()
    {
        try{
            clientSocket = serverSocket.accept();
        }
        catch(IOException ex) {
            System.out.println("Could not setup Client Socket.");
        }

        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(IOException ex) {
            System.out.println("Could not setup Streams");
        }

        try{
            String command = in.readLine();

            if(command.equals("init")) {
                out.println(directoryServer.getNextIP());
            } else {
                out.println("Unknown. Please Retry");
            }

        }
        catch(IOException ex) {
            System.out.println("TCP Server could not read instructions.");
        }
    }

    /**
     * stop - Closes all sockets related to TCP Server
     */

    public void stop()
    {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException ex) {
            System.out.println("Could not stop TCPServer. Please close Manually");
        }

    }
}

/**
 * TCPClient - This class is responsible for estabilishing a connection between associated Directory Server
 *             and the TCP Server of it's neighbor.
 *             Request the Neighboring Directory Server to return the Ip address associated with its neighbor
 */

class TCPClient {
    private DirectoryServer directoryServer;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * TCPClient - Constructor
     * @param ds - Directory Server associated with Client
     */

    public TCPClient(DirectoryServer ds)
    {
        directoryServer = ds;
    }

    /**
     * Creates sockets associated with TCP client
     * @param nextIP - IP associated with Directory Server's Successor
     * @param port - Port of machine
     */

    public void start(String nextIP, int port) {
        try {
            socket = new Socket(nextIP, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException ex) {
            System.out.println("Error estabilishing TCP Client. Resolve Manually.");
        }

    }

    /**
     * sendRequests - sends a command to the target Server
     * @param command - Request type for Server to handle
     * @return returns the IP Address of the Directory Server's Successor
     */

    public String sendRequest(String command) {
        out.println("init");

        try {
            String resp = in.readLine();
            return resp;
        }
        catch(IOException ex) {
            System.out.println("TCP Client unable to read response from Directory Server TCP connection.");

        }

        return "Unable to retrieve Neighbor Directory Server IP.";
    }

    /**
     * stop - stops the sockets, PrintWriter and BufferedReader associated with this TCP Client instance
     */

    public void stop() {
        try{
            in.close();
            out.close();
            socket.close();
        }
        catch(IOException ex) {
            System.out.println("Unable to end Directory Server TCP Client socket. Manual Intervention required");
        }

    }
}

/**
 * UDPServer - This class handles the requests sent from P2P Client to DHT
 */

class UDPServer extends Thread
{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];
    DirectoryServer directoryServer;


    /**
     * UDPServer - Constructor
     * @param port - port associated with local machine
     * @param ds - Associated Directory Server instance
     */
    public UDPServer(int port, DirectoryServer ds)
    {
        buffer = new byte[256];

        try {
            socket = new DatagramSocket(port);
        }
        catch (SocketException ex) {
            System.out.println("Unable to create UDP Server Datagram.");
        }

        directoryServer = ds;
        running = true;
    }

    /**
     * run - Executes the UDPServer within a thread
     *       Handles all requests sent by P2P client. This includes:
     *          init - find all IP of Directory Servers in DHT
     *          query - find Directory Server related to a specific contentName key
     *          exit - terminate all UDP related server sockets
     */

    public void run()
    {
        while(running)
        {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
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
                    List<String> result = directoryServer.getData(command.split(" ")[1]);

                    String combined = "";
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
            catch(IOException ex) {
                System.out.println("Unable to successfully resolve packets in UDP Server");
            }
        }
        socket.close();
    }
}

/**
 * DirectoryServer - This main class is responsible for estabilishing all responsibilities of the Directory Server
 */

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

    /**
     * DirectoryServer - Constructor
     * @param id - ID associated with Directory Server (I.e.: 1, 2, 3, 4)
     * @param port - port number associated with machine this instance is on
     */

    public DirectoryServer(int id, int port) {
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch(UnknownHostException ex) {
            System.out.println("Unable to find local machine IP.");
        }

        this.id = id;
        this.port = port;

        this.data = new HashMap<String, List<String>>();

        tcpClient = new TCPClient(this);

        startTCPServer();
        startUDPServer();
    }

    /**
     * StartUDPServer() - Initiates the UDP Server to run on a separate thread
     */

    public void startUDPServer() {
        udpServer = new UDPServer(port, this);
        udpServer.start();
    }

    /**
     * startTCPServer() - Initates TCP Server
     */
    public void startTCPServer() {
        TCPServer tcpServer = new TCPServer(port, this);
        tcpServer.start();
    }

    /**
     * stopTCPServer() - stops TCPServer from running
     */
    public void stopTCPServer() {
        tcpServer.stop();
    }

    /**
     * getID() - retrieves the ID of current Directory Server
     * @return - ID of Directory Server
     */
    public String getID() {
        return Integer.toString(id);
    }

    /**
     * getIPS() - retrieves the IP addresses of all Directory Servers on DHT
     * @return a String of IPS in the form of: IP1,IP2,IP3,IP4
     */
    public String getIPS() {
        String ipList = this.ip + "," + this.next;
        String neighbor = this.next;

        for (int i = 0; i < 2; i++) {
            tcpClient.start(neighbor, this.port);
            neighbor = tcpClient.sendRequest("init");
            ipList += "," + neighbor;
            tcpClient.stop();

        } // Only need two iterations since Node1 already knows Node1 and Node2.
        //   Each call returns it's next neighbor I.E. Node3 returns ip of Node4

        return ipList;
    }

    /**
     * setPrimary() sets the the First Directory Server
     * @param primaryNode - IP Address of Directory Server #1
     */
    public void setPrimary(String primaryNode) {
        primaryDS = primaryNode;
    }

    /**
     * setNeighbors() - sets the IPAddress of the Directory Server's predecessor and successor
     * @param prev - predecessor IP
     * @param next - successor IP
     */
    public void setNeighbors(String prev, String next) {
        this.prev = prev;
        this.next = next;
    }

    /**
     * getPrimaryDS() - retrieves the IP Address of Directory Server #1
     * @return return Directory Server #1 IP Address
     */
    public String getPrimaryDS() {
        return primaryDS;
    }

    /**
     * getNextIP() - Retrieves Directory Server's Successor IP Address
     * @return - returns an IP Address
     */
    public String getNextIP() {
        return next;
    }

    /**
     * getNeighbors() - returns the IP Address of the Directory Server's predecessor and successor
     * @return returns a list of strings
     */
    public List<String> getNeighbors() {
        List<String> neighbors = new ArrayList<String>();
        neighbors.add(this.prev);
        neighbors.add(this.next);

        return neighbors;
    }


    /**
     * getPort() - Retrieves the port number associated with Directory Server
     * @return an integer signifying local machine port number
     */
    public int getPort() {
        return port;
    }

    /**
     * getIP() - Retrieves the IP Address associated with local machine
     * @return - A String signifying an IP Address
     */

    public String getIP() {
        return ip;
    }

    /**
     * setData() - adds a (Key, Value) pair to the data object
     *             indicates that this Directory Server contains the IP Address of the
     *             location of the image.
     *
     * @param contentName - image name as key
     * @param location - IP address of where image is located
     */

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

    /**
     * getData() - Retrives the IP Address(es) of the image desired
     * @param contentName - key/image name
     * @return - returns a list of IP Addresses. More than one if >1 peer has the same image
     */
    public List<String> getData(String contentName) {
        return data.get(contentName);
        /** Add NULL Check **/
    }

    /**
     * removeData() - removes (Key, Value) pair
     * @param contentName - name of image
     * @param location - IP Address to delete
     * @return - a boolean signifying if process has successfully completed
     */
    public boolean removeData(String contentName, String location) {
        List<String> storedValues = data.get(contentName);

        if(storedValues != null && storedValues.contains(location)) {
            storedValues.remove(location);
            data.put(contentName, storedValues);
            return true;
        }

        return false;
    }

    /**
     * Main - Main method of Directory Server class
     * @param args - args[0] is the ID associated with Directory Server
     *               args[1] is the Port number associated with Directory Server
     */
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int id = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        DirectoryServer directoryServer = new DirectoryServer(id, port);
        directoryServer.startUDPServer();
        directoryServer.startTCPServer();

        if (in.nextLine().contains("exit")) {
            directoryServer.stopTCPServer();
        }
    }

}