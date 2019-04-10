import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class PSrv extends Thread
{
    private ServerSocket sSocket;
    Socket srv;
    
    public PSrv(int prt) throws IOException, ClassNotFoundException, Exception
    {
        sSocket = new ServerSocket(prt);
        System.out.println
            (
            "Server IP Address is: " +
            InetAddress.getLocalHost().getHostAddress().toString()
            );
    }
    
    public void run()
    {
        while (true)
        {
        try
        {
            srv = sSocket.accept();
            
            DataInputStream in = new DataInputStream(srv.getInputStream());
            DataOutputStream out = new DataOutputStream(srv.getOutputStream());
            
            switch (in.readUTF())
            {
                case "DOWNLOAD":
                    System.out.println("Download Request Recieved");
                    String imgName = in.readUTF();
                    
                    BufferedImage srvImg = ImageIO.read(new File("./img_s/" + imgName));
                    ImageIO.write(srvImg, "JPG", srv.getOutputStream());
                    ImageIO.write(srvImg, "JPG", srv.getOutputStream());
                    
                    System.out.println( imgName + " sent to Client");
                    break;
                    
                case "CLOSE":
                    System.out.println("Exit Command Received");
                    System.out.println("Shutting Down");
                    
                    srv.close();
                    System.exit(0);
                    break;
            }
        }
        catch( IOException IOe )
        {
            IOe.printStackTrace();
            break;
        }
        catch( Exception e)
        {
            System.out.println(e);
        }
        }
    }
    
    public static void main(String args[]) throws IOException, ClassNotFoundException, Exception
    {
        Thread t = new PSrv( Integer.parseInt(args[0]));
        t.start();
    }
}