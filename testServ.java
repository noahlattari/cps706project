import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class testServ extends Thread
{
	private ServerSocket sSocket;
	Socket server;
	
	public testServ(int port) throws IOException, ClassNotFoundException, Exception
	{
		sSocket = new ServerSocket(port);
		sSocket.setSoTimeout(180000);
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				server = sSocket.accept();
				
				// IO Communication between Server and Client
				DataInputStream bin = new DataInputStream(server.getInputStream());
                    // Read and output messages from Client
                    System.out.println(bin.readUTF());
                    System.out.println(bin.readUTF());
				
                DataOutputStream bout = new DataOutputStream(server.getOutputStream());
                    // Send messages to Client
                    bout.writeUTF("server: -i am greeting server");
                    bout.writeUTF("server:- hi! hello client");
				
                // Retrieve sent image from Client
				BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));
				System.out.println("Image recieved");
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
    	int port = Integer.parseInt(args[0]);
   		Thread t = new testServ(port);
    	t.start();
    }
}