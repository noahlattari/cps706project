import java.io.BufferedReader;
import java.io.*;
import java.net.*;

public class Client {
	
	public static void main(String arg[]) throws Exception
	{

		int test = hash("yeet");
		BufferedReader inFromUser =
				new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("Noahs-MacBook-Pro.local");
		System.out.println("xd");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String picName = inFromUser.readLine();
		





		
		
	}
	
	public static int hash(String fname)
	{
		char current;
		int ascii = 0;
		int total = 0;
		for(int i = 0; i < fname.length(); i++)
		{
			current = fname.charAt(i);
			ascii += current;
		}
		
		return ascii % 4;
	}
	
	
}