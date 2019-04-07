import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.ws.rs.core.Response.Status; // used to return status codes

public class FiftySixDHT implements Serializable
{
    private List<Node> nodes = new ArrayList<Node>();
    private int port;

    public FiftySixDHt()
    {
      port = 56;
      instantiateNodes();
      linkNodes();
    }

    public void instantiateNodes()
    {
        int num_nodes = 4;
        for (int i = 0; i < num_nodes; i++)
        {
            int id = i;
            //set up nodes;


        }
    }

    public void linkNodes()
    {

    }

    // if no content found must return HTTP response codes
    public Node findNode()
    {

    }

    // write file found in node to a serializable form
    private void writeObject(ObjectOutputStream out)
    {

    }

    // read serializable objects in
    private void readObject(ObjectInputStream in)
    {

    }







}