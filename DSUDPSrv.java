import java.net.DatagramSocket;
import java.net.DatagramPacket;


public class DSUDPSrv extends Thread
{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[256];

    public DSUDPSrv()
    {
        socket = new DatagramSocket(20650);
        running = true;
    }

    public void run()
    {
        while(running)
        {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buffer, buffer.length, adress, port);
            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("end"))
            {
                running = false;
                continue;
            }
            socket.send(packet);
        }
        socket.close();
    }
}