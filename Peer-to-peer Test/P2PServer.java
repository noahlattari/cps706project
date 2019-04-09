import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

// Peer-to-Peer Server

public class P2PServer
{
    final int status200 = 200; // OK
    final int status400 = 400; // Bad Request
    final int status404 = 404; // Not Found
    final int status505 = 505; // HTTP Version Not Support
    
    int i_pt;
    ServerSocket pSrv_tcp;
    
    public static ArrayList<UniqueTCP> pCli_uList = new ArrayList<UniqueTCP>();
    Thread pSrv_thread;
    
    public P2PServer(int pt)
    {
        i_pt = pt;
        try
        {
            pSrv_tcp = new ServerSocket(i_pt);
            
            pSrv_thread = new Thread(server_run);
            pSrv_thread.start();
        }
        catch( IOException e )
        {
            System.out.println("Port Not Available");
        }
    }
    
    Runnable server_run = new Runnable()
    {
    public void run()
    {
      while (true)
      {
          String msg;
          
          try
          {
              Socket pCli = pSrv_tcp.accept();
              
              DataInputStream in = new DataInputStream(pCli.getInputStream());
                msg = in.readUTF();
              
              Scanner read = new Scanner(msg);
              read.next();
              
              int pt = findAvailableUDPPort();
              pCli_uList.add(new UniqueTCP(pt));
                msg = status200 + " " + pt;
              
              DataOutputStream out = new DataOutputStream(pCli.getOutputStream());
                out.writeUTF(msg);
              
              pCli.close();
          }
          catch( IOException e )
          {
              System.out.println("Error in Connecting to Main Socket");
          }
      }
    }
    };
    
    public int findAvailableUDPPort()
    {
        int pt = 0;
        int pt_find = i_pt;
        
        boolean done = false;
        while( done == false )
        {
            try
            {
                ServerScoket tryPt = new ServerSocket(pt_find);
                done = true;
                tryPt.close();
                
                break;
            }
            catch( Exception e )
            {
                pt_find++;
            }
        }
        
        pt = pt_find;
        return pt;
    }
    
    // ----------
    // UNIQUE TCP
    // ----------
    
    public class UniqueTCP
    {   
        final int status200 = 200; // OK
        final int status400 = 400; // Bad Request
        final int status404 = 404; // Not Found
        final int status505 = 505; // HTTP Version Not Support
        
        ServerSocket uTCPS;
        Thread TCP_thread;
        
        public UniqueTCP(int pt)
        {
            try
            {
                uTCPS = new ServerSocket(pt);
                
                TCP_thread = new Thread(tcp_run);
                TCP_thread.start();
            }
            catch( IOException e )
            {
                System.out.println("Port Not Available");
            }
        }
        
        Runnable tcp_run = new Runnable()
        {
        public void run()
        {
            try
            {
                byte[] fin_BytesArray = null;
                String msg = "";
                String fileName = "";
                String request = "";
                String HTTPver = "";
                String HTTPrsp = "";
                String connect = "";
                String contentType = "";
                String timeString = getCurrentTime();
                
                Socket socket = uTCPS.accept();
                
                DataInputStream in = new DataInputStream(socket.getInputStream());
                    msg = in.readUTF();
                
                Scanner read = new Scanner(msg);
                request = read.next();
                
                if(request.equals("GET"))
                {
                    fileName = read.next();
                    HTTPver = read.next();
                    connect = "Close";
                    contentType = "image/jpeg";
                    
                   if(HTTPver.equals("HTTP/1.1"))
                   {
                       fileName = fileName.substring(1);
                       File f = new File(fileName);
                       
                       try
                       {
                           String fileNA = "";
                           String fileNB = "";
                           
                           fileNA = fileName.substring(0, fileName.indexOf(".jpeg"));
                           fileNB = fileName.substring(0, fileName.indexOf(".jpeg"));
                           
                           fileNA += "---TRIAL---.jpeg";
                           
                           File badFN = new File(nFA);
                           badFN.createNewFile();
                           badFN.delete();
                           
                           File f2 = new File(fileNB + ".jpg");
                           
                           if (f2.exists())
                               f = new FIle(filenB + ".jpg");
                           
                           if (f.exists())
                           {
				               double fileSizeBytes = f.length();
				               
                               String lastMod = getFileModifiedTime(f);
				               HTTPrsp = createHTTPResponse(status200, timeString, lastMod, "bytes", Integer.toString((int) fileSizeBytes), connect, contentType);
				               
                               byte[] httpToBytes = HTTPrsp.getBytes(Charset.forName("UTF-8"));
								
                               FileInputStream fileIS = null;
				               byte[] fileToBytes = new byte[(int) f.length()];
									
				               fileIS = new FileInputStream(f);
				               fileIS.read(fileToBytes);
				               fileIS.close();

				               fin_BytesArray = new byte[httpToBytes.length + fileToBytes.length];
				               System.arraycopy(httpToBytes, 0, fin_BytesArray, 0, httpToBytes.length);
				               System.arraycopy(fileToBytes, 0, fin_BytesArray, httpToBytes.length, fileToBytes.length);
				            }
                            else
                            {
                                HTTPrsp = createHTTPResponse(status404, timeString, null, null, null, connect, null);
                                fin_BytesArray = HTTPrsp.getBytes(Charset.forName("UTF-8"));
                            }
                           
                       }
                       catch( Exception e )
                       {
                           HTTPrsp = createHTTPResponse(status400, timeString, null, null, null, connect, null);
                           fin_BytesArray = HTTPrsp.getBytes(Charset.forName("UTF-8"));
                       }
                   }
                   else
                   {
                       HTTPrsp = createHTTPResponse(status505, timeString, null, null, null, connect, null);
                       fin_BytesArray = HTTPrsp.getBytes(Charset.forName("UTF-8"));
                   }
                }
                
                OutputStream srv_out = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(srv_out);
                    out.writeInt(fin_BytesArray.length);
                    out.write(fin_BytesArray, 0, fin_BytesArray.length);
                
                socket.close();
                uTCPS.close();
                
                for (int i = 0; i < P2PServer.pCli_uList.size(); i++)
                {
                    if ( P2PServer.pCli_uList.get(i).equals(this))
                    {
                        P2PServer.pCLI_uList.remove(i);
                        TCP_thread.stop();
                        break;
                    }
                }
            }
            catch( Exception e ) {}
        }
        };
        
        // ------------
        // TIME UTILITY
        // ------------
        
        public String getCurrentTime()
        {
            Date date = new Date();
            Scanner read = new Scanner(date.toString());
            
            String day_name = read.next();
            String month = read.next();
            String day_numb = read.next();
            
            DateFormat timeFormat = new SimpleDateFormat("yyy HH:mm:ss");
            Date time = new Date();
            
            return (day_name + ", " + day_numb + " " + month + " " + timeFormat.format(time) + " GMT");
        }
        
        public String getFileModifiedTime(File f)
        {
            Date date = new Date(f.lastModified());
            Scanner read = new Scanner(date.toString());
            
            String day_name = read.next();
            String month = read.next();
            String day_numb = read.next();
            
            DateFormat timeFormat = new SimpleDateFormat("yyy HH:mm:ss");
            Date time = new Date(f.lastModified());
            
            return (day_name + ", " + day_numb + " " + month + " " + timeFormat.format(time) + " GMT");
        }
        
        // -------------
        // HTTP RESPONSE
        // -------------
        
        public String createHTTPResponse( int code, String currDate, String fModDate, String acceptRange, String len, String connect, String contentType);
        {
            String rsp = "";
            
            if (code = status200)
            {
                rsp += "HTTP/1.1 " + code + " " + "OK\r\n";
				rsp += "Connection: " + connect + "\r\n";
				rsp += "Date: " + currDate + "\r\n";
				rsp += "Last-Modified: " + fModDate + "\r\n";
				rsp += "Accept-Ranges: " + acceptRange + "\r\n";
				rsp += "Content-Length: " + len + "\r\n";
				rsp += "Content-Type: " + contentType + "\r\n\r\n";
            }
            else
            {
				if (code == statusCode400)
                {
					rsp += "HTTP/1.1 " + code + " " + "Bad Request\r\n";
				} 
				else if (code == statusCode404)
                {
					rsp += "HTTP/1.1 " + code + " " + "Not Found\r\n";
				} 
				else if (code == statusCode505)
                {
					rsp += "HTTP/1.1 " + code + " "
							+ "HTTP Version Not Supported\r\n";
				}
				rsp += "Connection: " + connect + "\r\n";
				rsp += "Date: " + currDate + "\r\n\r\n";
			}            
            return rsp;
        }
        
    }
}