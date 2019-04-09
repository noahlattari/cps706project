import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// Peer-to-Peer Client

public class P2PClient
{
    static String iDHT_ip;  // Inital DHT IP Address
    static int iDHT_pt;     // Initial DHT Port
    static int pSrv_pt;     // Client Server Port
    
    public static void main(String[] args)
    {
        Thread t;
        
        // Retrieve client server port
        pSrv_pt = Integer.parseInt(args[0]);
        
        // Retrieve initial DHT server IP and port
        iDHT_ip = args[1];
        iDHT_pt = Integer.pariseInt(args[2]);
        
        t = new Thread(client_run);
        t.start();
    }
    
    static Runnable client_run = new Runnable()
    {      
        // Determines the DHT server ID to connect to
        public int calculateServerID(String input)
        {
            int calculatedID = 0;
            for (int i = 0; i < input.length(); i++)
                calculatedID += (int) input.charAt(i);
        
            return calculatedID % 4;
        }
    
    // Runs client
    public void run()
    {
        System.out.println("Initializing Client...");
        
        Client pCli = new Client(iDHT_ip, iDHT_pt, pSrv_pt);
        P2PServer pSrv = new P2PServer(pSrv_pt);
        
        System.out.println("Client running. \n")
        
        Scanner read = new Scanner(System.in);
        String userInput;
        
        while (true)
        {
            System.out.println("Input: [U]pload | [Q]uery | [E]xit");
            System.out.print("Please enter: ");
            userInput = read.next();
            
            switch(userInput = userInput.toLowerCase())
            {
                // What file you want to upload?
                case "u":
                    System.out.print("\n");
                    System.out.print("Enter Filename: ");
                    userInput = read.next();
                    
                    int dstSrv_id = calculateServerID(userInput);
                    
                    try
                    {
                        // Upload to calculated server destination
                        pCli.upload(dstSrv_id, userInput);
                    }
                    catch( Exception e )
                    {
                        System.out.println("Server connection failure.");
                    }
                    System.out.print("\n");
                    break;
                
                // What file do you want to find/download?
                case "q":
                    System.out.print("\n");
                    System.out.print("Enter Filename: ");
                    userInput = read.next();
                    
                    int dstSrv_id = calculatedServerID(userInput);
                    
                    try
                    {
                        // Query to calculated server destination
                        pCli.query(dstSrv_id, userInput);
                    }
                    catch( Exception e )
                    {
                        System.out.println("Server connection failure.");
                    }
                    System.out.print("\n");
                    break;
                    
                // Exit
                case "e":
                    System.out.println("\n");
                    try
                    {
                        // Close client
                        pCli.exit();
                    }
                    catch( Exception e )
                    {
                        System.out.println("Server connection failure.")
                    }
                    break;
                
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
    };
    
    public static class Client 
    {
        int pSrv_pt;        // Client server port
        String[] fileName;  // List of file names
         
        int[] DHP_pts = new int[4];         // List of the 4 DHT server ports
        String[] DHP_ips = new String[4];   // List of the 4 DHT server IPs
        DatagramSocket cUDPS;               // Client UDP socket
        
        // Client Constructor
        public Client(String iDHP_ip, int iDHP_pt, int pSrv_pt)
        {
            this.pSrv_pt = pSrv_pt;
            this.DHP_ips[0] = iDHP_ip;
            this.DHP_pts[0] = iDHP_pt;
            
            try
            {
                cUDPS = new DatagramSocket();
                init();
            }
            catch( Exception e ) { }
        }
        
        // Client Initialization
        public void init() throws Exception
        {
            String msg;
            String status;
            
            sndData("GET ALL IP", DHP_ips[0], DHP_pts[0]);
            
            msg = rcvData();
            Scanner read = new Scanner(msg);
            
            status = read.next();
            for (int i = 0; i < 5; i++ )
                read.next();
            
            if (status.equals("200"))
                System.out.printl("Server: Client Initialized");
            
            // Array for all DHT IPs and port numbers
            for (int i = 0; i < 4; i++)
            {
                DHT_ips[i] = read.next();
                DHT_pts[i] = Integer.parseInt(read.next());
            }
        }
        
        // ------------------
        // USER INPUT SECTION
        // ------------------
        
        // Client Image Upload
        public void upload(int id, String fileName) throws Exception
        {
            String status;
            String msg =
                "UPLOAD " + fileName + " " +
                InetAddress.getLocalHost().getHostAddress() + " Padding";
            
            sndData(msg, DHT_ips[id]. DHT_pts[id]);
            
            msg = rcvData();
            Scanner read = new Scanner(msg);
            status = read.next();
            
            if (status.equals("200"))
                System.out.println("Server: File successfully added");
        }
        
        // Client Image Query
        public void query(int id, String fileName) throws Exception
        {
            String cliContact;
            String status;
            String msg = "QUERY " + fileName + " Padding";
            
            sndData(msg, DHT_ips[id], DHT_pts[id]);
            
            msg = rcvData();
            Scanner read = new Scanner(msg);
            status = read.next();
            
            if (status.equals("404"))
                System.out.println("Server: Content not found");
            else if (status.equals("200"))
            {
                System.out.println("Server: Content found, IP sent");
                read = new Scanner(msg);
                
                read.next();
                cliContact = read.next();
                
                String request = 
                    createHTTPRequest
                    (
                    "GET", fileName, "Close",
                    InetAddress.getByName(cliContact).getHostName(),
                    "image/jpeg", "en-us"
                    );
                
                msg = connectToPeerServer("OPEN " + fileName, cliContact, pSrv_pt);
                
                read = new Scanner(msg);
                status = read.next();
                
                int newPort = read.nextInt();
                
                if (status.equals("200"))
                {
                    System.out.println("Peer Server: New connection open on port " + newPort);
                    System.out.println
                        (
                        "--HTTP Request Sent to Server--START\n" +
                        request +
                        "--HTTP Request Sent to Server--END\n"
                        );
                    
                    connectToUniqueServer(fileName, request, cliContact, newPort);
                }
            }
        }
        
        // Client Exit
        public void exit() throws Exception
        {
            byte[] rcvData = new byte[1024];
            String status;
            String msg = "EXIT " +
                DHT_pts[0] +
                DHT_pts[1] +
                DHT_pts[2] +
                DHT_pts[3] + " Padding";
            
            sndData(msg, DHT_ips[0], DHT_pts[0]);
            
            msg = rcvData();
            cUDPS.close();
            
            Scanner read = new Scanner(msg);
            status = read.next();
            
            if (status.equals("200"))
                System.out.println("Server: All contents removed successfully");
            
            System.exit(0);
        }
        
        // -----------------
        // FILE SEND/RECEIVE
        // -----------------
        
        public void sndData(String msg, String srv_ip, int srv_pt) throws IOException
        {
            byte[] sndData = new byte[1024];
            sndData = msg.getBytes();
            
            InetAddress iAddress = InetAddress.getByName(srv_ip);
            DatagramPacket sndPkt = 
                new DatagramPacket(sndData, sndData.length, iAddress, srv_pt);
            
            cUDPS.send(sndPkt);
        }
        
        public void rcvData() throws IOException
        {
            byte[] rcvData = new byte[1024];
            DatagramPacket rcvPkt = 
                new DatagramPacket(rcvData, rcvData.length);
            
            cUDPS.receive(rcvPkt);
            return new String(rcvPkt.getData());
        }
        
        // ------------------
        // SERVER CONNECTIONS
        // ------------------
        
        public String connectToPeerServer(String msg, String ip, int pt)
            throws UnknownHostException, IOException
        {
            Socket pSrv_connect = new Socket(ip, pt);  
            OutputStream srv_out = pSrv_connect.getOutputstream();
            
            // Send message to Server
            DataOutputStream out = new DataOutputStream(srv_out);
                out.writeUTF(msg);
            
            // Recieve message from Server
            DataInputStream in = new DataInputStream(srv_connect.getInputStream());
                msg = in.readUTF();
            
            pSrv_connect.close();
            return message;
        }
        
        public void connectToUniqueServer(String fileName, String request, String ip, int pt)
            throws UnknownHostException, IOException
        {
            Socket uSrv_connect = new Socket(ip, pt);
            
            OutputStream srv_out = uSrv_connect.getOutputStream();
            DataOutputStream out = new DataOutputStream(srv_out);
                out.writeUTF(request);
            
            InputStream srv_in = uSrv_connect.getInputStream();
            DataInputStream in = new DataInputStream(srv_in);
                int len = in.readInt();
            
            if ( len > 0 )
                in.readFully(data);
            
            uSrv_connect.close();
            
            // Connection ends here
            // Reading response starts
            
            String rsp = new String(data);
            Scanner read = new Scanner(rsp);
            
            String status = read.nextLine() + "\r\n";
            String temp;
            
            if (status.contains("HTTP/1.1 200 OK"))
            {
                status = getHTTPResponse(read, status);
                
                File fileOut = new File(fileName + "jpeg");
                int fileSize = data.length - status.getBytes().length;
                byte[] toBytes = new byte[fileSize];
                
                for (int i = status.getBytes().length; i < data.length; i++ )
                    toBytes[i - status.getBytes().length] = data[i];
                
                FileOutputStream fos = new FileOutputStream(fileOut);
                fos.write(toBytes);
                fos.close();
            }
            else if(status.contains("HTTP/1.1 400 Bad Request"))
            {
                status = getHTTPResponse(read, status);
            }
            else if(status.contains("HTTP/1.1 404 Not Found"))
            {
                status = getHTTPResponse(read, status);
            }
            else if(status.contains("HTTP/1.1 505 HTTP Version Not Support"))
            {
                status = getHTTPResponse(read, status);
            }
            
            System.out.println
                (
                "--HTTP Response Got From Server--START\n" +
                status +
                "--HTTP Response Got From Server--END"
                );
        }
        
        // ----------------
        // HTTP CONNECTIONS
        // ----------------
        
        public String getHTTPResponse(Scanner read, String rsp)
        {
            String temp;
            
            while (read.hasNext())
            {
                temp = read.nextLine() + "\r\n";
                rsp += temp;
                
                if (temp.equals("\r\n"))
                    break;
            }
            
            return rsp;
        }
        
        public String createHTTPRequest(String request, String object, String connection, String host, String acceptType, String acceptLan) {
			String req = "";
			req += request + " /" + object + ".jpeg" + " HTTP/1.1\r\n";
			req += "Host: " + host + "\r\n";
			req += "Connection: " + connection + "\r\n";
			req += "Accept: " + acceptType + "\r\n";
			req += "Accept-Language: " + acceptLan + "\r\n\r\n";
			return req;
		}
    }
}