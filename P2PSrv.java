import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class P2PSrv extends Thread
{
	private ServerSocket sSocket;
	Socket server;
	
	public P2PSrv(int port) throws IOException, ClassNotFoundException, Exception
	{
		sSocket = new ServerSocket(port);
		sSocket.setSoTimeout(10000);
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				server = sSocket.accept();
				
				// IO Communication between Server and Client
				DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    // Send messages to Client
                    out.writeUTF("Server: Connection Established\n");
                    // Read and output messages from Client
                    System.out.println(in.readUTF());   
                		
                // Retrieve sent image from Client
                String imgName = in.readUTF();
				BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));
                File download = new File("./srv_img/" + imgName);
                ImageIO.write(img, "jpg", download);
                
                System.out.println("Successful retrieval and download of client's image\n");
			}
            catch(SocketTimeoutException st)
            {
                System.out.println("Socket timed out!");
                break;
            }
            catch(IOException e)
            {
                  e.printStackTrace();
                  break;
            }
            catch(Exception ex)
            {
                  System.out.println(ex);
            }
		}
	}
	
	public static void main(String [] args) throws IOException, ClassNotFoundException, Exception
    {
   		Thread t = new P2PSrv( Integer.parseInt(args[0]));
    	t.start();
    }
}