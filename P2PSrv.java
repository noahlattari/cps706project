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
                                
                // Image to send
                String imgName = in.readUTF();
                BufferedImage serverImg = ImageIO.read(new File("./img_s/" + imgName));
            
                // Send image
                ImageIO.write(serverImg, "JPG", server.getOutputStream());
                System.out.println("Image successfully sent to Client");

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