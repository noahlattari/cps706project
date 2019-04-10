import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

/**
 * PSrv, or Peer Server, connects to Peer Client (local or otherwise).
 *
 * It operates and waits for either two commands: DOWNLOAD or CLOSE.
 * Upon receiving DOWNLOAD, it will read the desired image file and send it
 * Upon receiving CLOSE, it will shut down alongside the Peer Client connected
 */
public class PSrv extends Thread
{
    private ServerSocket sSocket;
    Socket srv;
    
    /**
     * Peer Server
     * Creates the Server Socket for Peer Client to connect to and
     * outputs the IP Address which others can use to connect
     *
     * @param   Port Number
     */
    public PSrv(int prt) throws IOException, ClassNotFoundException, Exception
    {
        // Create Server Socket for Peer Clients to connect to
        sSocket = new ServerSocket(prt);
        // Print IP Address of running machine so others can connect to
        System.out.println
            (
            "Server IP Address is: " +
            InetAddress.getLocalHost().getHostAddress().toString()
            );
    }
    
    /**
     * Runs thread waiting for either the DOWNLOAD or CLOSE command
     * from the Peer Client
     */
    public void run()
    {
        while (true)
        {
        try
        {
            // Accept connection
            srv = sSocket.accept();
            
            // I/O Communication Channels between Client and Server
            DataInputStream in = new DataInputStream(srv.getInputStream());
            DataOutputStream out = new DataOutputStream(srv.getOutputStream());
            
            // Wait for Client Commmand
            switch (in.readUTF())
            {
                // Recieves desired image and sends it to Client
                case "DOWNLOAD":
                    System.out.println("Download Request Recieved");
                    String imgName = in.readUTF();
                    
                    BufferedImage srvImg = ImageIO.read(new File("./img_s/" + imgName));
                    ImageIO.write(srvImg, "JPG", srv.getOutputStream());
                    ImageIO.write(srvImg, "JPG", srv.getOutputStream());
                    
                    System.out.println( imgName + " sent to Client");
                    break;
                    
                // Closes upon given the command
                case "CLOSE":
                    System.out.println("Exit Command Received");
                    System.out.println("Shutting Down");
                    
                    srv.close();
                    System.exit(0);
                    break;
            }
        }
        catch( IOException IOe )
        {
            IOe.printStackTrace();
            break;
        }
        catch( Exception e)
        {
            System.out.println(e);
        }
        }
    }
    
    /**
     * Main body of Peer Server
     * Creates and starts thread 
     *
     * @param   Port Number
     */
    public static void main(String args[]) throws IOException, ClassNotFoundException, Exception
    {
        Thread t = new PSrv( Integer.parseInt(args[0]));
        t.start();
    }
}