import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

// Client to send image to server
public class testClient
{
    
    public static void main(String args[]) throws Exception
    {
        String server = "localhost";
        int port = Integer.parseInt(args[0]);
    
        String imgName = args[1]; //dicedog.jpg
        // Connect to server and send image
        try
        {
            System.out.println("Attempting connection to " + server + ":" + port);
            Socket client = new Socket(server,port);
            System.out.println("Successful connection to " + client.getRemoteSocketAddress() + "\n");         
            
            // I/O Communication between Client and Server.
            DataInputStream in = new DataInputStream(client.getInputStream());
                 // Retrieve and print message from server
                System.out.println(in.readUTF());
                System.out.println(in.readUTF());
            
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
                // Send messages to server
                out.writeUTF("Hello from " + client.getLocalSocketAddress());
                out.writeUTF("client: hello to server");

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