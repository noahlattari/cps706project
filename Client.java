import java.io.BufferedReader;

public class Client {
	
	public static void main(String arg[]) 
	{
		//this is where u read in input from command line and connect to the server datagram thing
		
		int test = hash("yeet");
		
		
		System.out.println(test);
		
		
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