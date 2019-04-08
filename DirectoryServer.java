import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.lang;


//What does node need to contain?

//Param:
//  ID (2^n-1)
//  UDP port for P2P Client (init, get, update, exit)
//  TCP port for Node-Node Communication
//  Child node
//  Parent node
//  IP
//  Table (content name, IP)


//Functions
//  Constructor (grabs IP) x
//  linkNodes x
//  Store content IP (read buffer) x
//  return content IP (write buffer) x
//  create client socket
//  create server socket
//  main function (instantiate the nodes)


public class DirectoryServer implements Serializable extends Thread {
    private int id;
    private int ip;
    private int udpPort;
    private int tcpPort;
    private String predecessor;
    private String sucessor;
    private Map<String, Integer> data;

    //UDP - Client-DS
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];

    //TCP - DS-DS - Server
    DatagramSocket serverSocket = new DatagramSocket(9876) //temp port
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];

    //TCP - DS-DS - Client
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName("Noahs-MacBook-Pro.local");
    boolean serverOn = true;s

    public DirectoryServer(int id) {
        this.id = id;
        //this.ip = ;
        this.udpPort = 20650;
        this.tcpPort = 20650;

        //Setup UDP for Client-DS
        this.socket = new DatagramSocket(this.udpPort);
    }

    public void linkDS(String child, String parent) {
        this.predecessor = child;
        this.sucessor = parent;
    }

    public void storeData(String contentName, int clientIP) {
        int reponseCode = 200;
        this.data.put(contentName, clientIP);

        //do something with responseCode
    }

    public int getData(String contentName) {
        int clientIp = this.data.get(contentName);
        if (clientIp != null) {
            return clientIp;
        } else {
            return 404; // change to HTTP Response Codes
        }
    }

    // UDP for Client-DS
    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port); // return message

            String request = new String(packet.getData(), 0, packet.getLength());

            // evaluate request messages //init, get, update, exit
            switch (request) {
                case "init":
                    break;

                case "update":
                    break;

                case "query":
                    break;
                case "exit":
                    running = false;
                    continue;
                    break;
                default:
                    //throw exception
            }


            socket.send(packet);
        }
        socket.close();
    }

    // TCP for DS-DS-Server
    public void runServerTCP() {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        String sentence = new String(receivePacket.getData());

        InetAddress IPAddress = receivePacket.getAddress();

        int port = receivePacket.getPort();

        //String capitalizedSentence = sentence.toUpperCase();
        //sendData = capitalizedSentence.getBytes();
        String code = "200";
        sendData = code.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

        serverSocket.send(sendPacket);
    }

    // TCP for DS-DS-Client
    public void runClientTCP() {
        while(serverOn)
        {

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            String sentence = inFromUser.readLine();
            sendData = sentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String result = new String(receivePacket.getData());

            System.out.println("FROM SERVER:" + result);

            if(sentence.toUpperCase.equals("EXIT"))
            {
                serverOn = false;
            }

        }
        clientSocket.close();

    }
 
    public static void main(String args[])
    {
        //Instantiate variables
        Scanner scanner = New Scanner(System.in);
        int id = Integer.parseInt(args[0]);
        Thread t = new DirectoryServer(id);

        //Get Machine IP
        InetAddress ip = InetAddress.getLocalHost();
        int address = ip.getHostAddress();
        System.out.println("Dir. Server - ID: " + id + " IP Address: " + address);

        //Set Neighbor IPs
        System.out.println("Enter Child IP (space) Parent ID: ");
        String[] neighbors = scanner.nextLine().split("\\s+");
        t.linkDS(neighbors[0], neighbors[1]);
        
        
        
        
        
    }
}
