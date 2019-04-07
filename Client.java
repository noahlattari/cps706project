import java.io.BufferedReader;
import java.io.*;
import java.net.*;

public class Client {
	
	public static void main(String arg[]) throws Exception
	{
		/*
		int test = hash("yeet");
		BufferedReader inFromUser =
				new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("Noahs-MacBook-Pro.local");
		System.out.println("xd");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String picName = inFromUser.readLine();
		
		*/
		

		

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("Noahs-MacBook-Pro.local");
		boolean serverOn = true;

		

		while(serverOn)
		{
			
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];

			String sentence = inFromUser.readLine();
			sendData = sentence.getBytes();

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
		
			String result = new String(receivePacket.getData());

			System.out.println("FROM SERVER:" + result);

			if(sentence.toUpperCase.equals("EXIT"))
			{
				serverOn = false;
			}
	
		}
		clientSocket.close();	
		
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