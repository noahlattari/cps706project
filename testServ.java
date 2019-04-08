import java.net.*;
import java.io.*;

public class Server extends Thread
{
	private ServerSocket sSocket;
	Socket server;
	
	public Server(int port) throws IOException, SQLException, ClassNotFoundException, Exception
	{
		sSocket = new ServerSocket(port);
		sSocket.setSoTimeout(180000);
	}
	
	public void run()
	{
		while true()
		{
			try
			{
				server = sSocket.accept();
				
				/* Communication
				DataInputStream in = new DataInputStream(server.getInputStream());
				DataOutpoutStream out = new DataOutputStream(server.getOutputStream());
				*/
				
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
	
	public static void main(String [] args) throws IOException, SQLException, ClassNotFoundException, Exception
    {
    	//int port = Integer.parseInt(args[0]);
   		Thread t = new GreetingServer(6066);
    	t.start();
    }
}