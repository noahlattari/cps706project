import java.net.*;
import java.io.*;
import java.awt.*;
import javax.imageio.*;

// Client to send image to server
public class Client
{
    BufferedImage clientImg;
    
    public static void main(String arg[]) throws Exception
    {
        String server = "SERVERNAME";
        int port = PORTNUMBER;
    
        // Connect to server and send image
        try
        {
            System.out.println("Attempting connection to " + server + ":" + port);
            Socket client = new Socket(server,port);
            
            System.out.println("Successful connection to " + client.getRemoteSocketAddress() + "\n");
            
            /* 
            	Shows Communication between Client and Server.
          		DataInputStream in = new DataInputStream(client.getInputStream()); 
            	DataOutputStream out = new DataOutputStream(client.getOutputStream());
            */
            
            bClientImg = ImageIO.read(new File("FILE HERE"));
            
            // Send image
            ImageIO.write(bClientImg, "JPG", client.getOutputStream());
            System.out.println(f"Image sent to client");
            client.close();
        }
        catch( IOException e)
        {
            e.printStackTrace();
        }
        
    }

}