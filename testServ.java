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
				DataInputStream bin = new DataInputStream(server.getInputStream());
                DataOutputStream bout = new DataOutputStream(server.getOutputStream());
                    
                    // Send messages to Client
                    bout.writeUTF("server: -i am greeting server");
                    bout.writeUTF("server:- hi! hello client");
                    // Read and output messages from Client
                    System.out.println(bin.readUTF());
                    System.out.println(bin.readUTF());
				
                // Retrieve sent image from Client
                String imgName = bin.readUTF();
				BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));
                File download = new File("./serv_img/" + imgName);
                ImageIO.write(img, "jpg", download);
                
                System.out.println("Image recieved and downloaded from client");
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