import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

// Client to send image to server
public class P2PCli
{
    
    public static void main(String args[]) throws Exception
    {
        String dest_serv = "localhost";
        int serv_port = Integer.parseInt(args[0]);
        
        String imgName = args[1];
        
        // Connect to server and send image
        try
        {
            System.out.println("Attempting connection to " + dest_serv + ":" + serv_port);
            Socket client = new Socket(dest_serv, serv_port);
            System.out.println("Successful connection to " + client.getRemoteSocketAddress() + "\n");         
            
            // I/O Communication between Client and Server.
            DataInputStream in = new DataInputStream(client.getInputStream());
                 // Retrieve and print message from server
                System.out.println(in.readUTF());
            
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
                // Send messages to server
                out.writeUTF("Client: Connection Established\n");

            // Image to send
            out.writeUTF(imgName);
            BufferedImage clientImg = ImageIO.read(new File("./cli_img/" + imgName));
            
            // Send image
            ImageIO.write(clientImg, "JPG", client.getOutputStream());
            System.out.println("Image successfully sent to server");

            client.close();
        }
        catch( IOException e)
        {
            e.printStackTrace();
        }
        
    }

}