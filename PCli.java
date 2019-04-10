import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

import java.util.Scanner;

// Peer-to-peer Client
public class PCli
{
    
    public static void main(String args[]) throws Exception
    {
        InetAddress dst_srv = InetAddress.getByName(args[0]);
        int srv_prt = Integer.parseInt(args[1]);
        
        System.out.println("Connecting to " + dst_srv + ":" + srv_prt);
        Socket cli = new Socket(dst_srv, srv_prt);
        System.out.println("Successfully connected to " + cli.getRemoteSocketAddress() + "\n");
        
        DataInputStream in = new DataInputStream(cli.getInputStream());     
        DataOutputStream out = new DataOutputStream(cli.getOutputStream());
        
        Scanner read = new Scanner(System.in);
        String uInput;
        
        while ( true )
        {
            System.out.println("Options: [D]ownload | [E]xit");
            System.out.print("Select option: ");
            uInput = read.next();
            
            String uSel = uInput.toLowerCase();
            
            switch( uSel )
            {
                case "d":
                    System.out.println("\nWhich image do you want to download?");
                    System.out.print("Input name: ");
                        uInput = read.next();
                    
                    out.writeUTF("DOWNLOAD");
                    out.writeUTF(uInput);
                    
                    BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(cli.getInputStream()));
                    
                    File download = new File("./img_c/" + uInput);
                    ImageIO.write(img, "jpg", download);
                
                    System.out.println("Successfully downloaded " + uInput + "\n");
                    cli.close();
                    System.exit(0);
                    break;
                    
                case "e":
                    System.out.println("\nShutting Down");
                    
                    out.writeUTF("CLOSE");
                    
                    cli.close();
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("Invalid selection");
                    break;
            }
        }
    }
}