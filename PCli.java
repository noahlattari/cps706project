import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.Scanner;

/**
 * PCli, or Peer Client, connects to a Peer Server (local or otherwise)
 * given the InetAddress (Name or literal IP), and port number.
 *
 * The user is given two options: [D]ownload or [E]xit
 * If user selects [D]ownload, they are prompted to give the image file name
 * that they wish to download. It will send a DOWNLOAD message to the Peer Server
 * and the server will send the image.
 * If user selects [E]xit, both Peer Client and Peer Server will shut down.
 */
public class PCli
{ 
    /**
     * Main method of Peer Client
     * @param   IP Address/Server Name of Peer Server (e.g. "localhost") to connect to
     * @param   Port number of the Peer Server to connect to
     */
    public static void main(String args[]) throws Exception
    {
        InetAddress dst_srv = InetAddress.getByName(args[0]);      // IP Address/Name
        int srv_prt = Integer.parseInt(args[1]);                   // Server Port Number
        
        System.out.println("Connecting to " + dst_srv + ":" + srv_prt);
        // Create Peer Client Socket connecting to Peer Server
        Socket cli = new Socket(dst_srv, srv_prt);
        System.out.println("Successfully connected to " + cli.getRemoteSocketAddress() + "\n");
        
        // I/O Communication between Client and Server
        DataInputStream in = new DataInputStream(cli.getInputStream());     
        DataOutputStream out = new DataOutputStream(cli.getOutputStream());
        
        // Reads user input
        Scanner read = new Scanner(System.in);
        String uInput;
        
        while ( true )
        {
            System.out.println("Options: [D]ownload | [E]xit");
            System.out.print("Select option: ");
            // Reads selection
            uInput = read.next();
            
            String uSel = uInput.toLowerCase();
            
            switch( uSel )
            {
                // [D]ownload Image
                case "d":
                    System.out.println("\nWhich image do you want to download?");
                    System.out.print("Input name: ");
                        // Reads image file name that user client
                        uInput = read.next();
                    
                    // Sends DOWNLOAD command to server alongside desired image
                    out.writeUTF("DOWNLOAD");
                    out.writeUTF(uInput);
                    
                    // Reads image data given by Server
                    BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(cli.getInputStream()));
                    
                    // Writes and saves the image data into a folder
                    File download = new File("./img_c/" + uInput);
                    ImageIO.write(img, "jpg", download);
                
                    System.out.println("Successfully downloaded " + uInput + "\n");
                    cli.close();
                    System.exit(0);
                    break;
                
                // [E]xit both Peer Client and Peer Server
                case "e":
                    System.out.println("\nShutting Down");
                    
                    out.writeUTF("CLOSE");
                    
                    cli.close();
                    System.exit(0);
                    break;
                
                // Any inputted command is invalid
                default:
                    System.out.println("Invalid selection");
                    break;
            }
        }
    }

    public static int modHash(String content_name)
    {
        int x = 0;
        for (int i = 0; i < content_name.length(); i++)
        {
            char character = content_name.charAt(i);
            int ascii = (int) character;

            x += ascii;
        }

        int y = x % 4;

        return y;
    }
}