import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.ws.rs.core.Response.Status; // used to return status codes

public class FiftySixDHT implements Serializable
{
    private List<Node> nodes;
    private int port;

    public FiftySixDHt()
    {
      port = 56;
      nodes = new ArrayList<Node>();
      instantiateNodes();
      linkNodes();
    }

    public void instantiateNodes()
    {
        int num_nodes = 4;
        for (int i = 1; i <= num_nodes; i++)
        {
            InetAddress addr = InetAddress.getByName("127.0.0." + Integer.toString(i));
            Node n = new Node(i, addr, port);
            nodes.add(n);
        }
    }

    public void linkNodes()
    {
        for (int i = 0; i < this.nodes.size(); i++)
        {
            if (i == 0)
            {
                this.nodes.linkNodes(this.nodes.get(3), this.nodes.get(1));
            }
            else
            {
                this.nodes.linkNodes(this.nodes.get(i-1), this.nodes.get(i+1));
            }
        }
    }

    // if no content found must return HTTP response codes
    public Node findNode(String content_name)
    {
        Node n;
        int target = modHash(content_name);
        for (int i = 0; i < this.nodes.size(); i++)
        {
            if (this.nodes.get(i).id == target)
            {
                n = this.nodes.get(i);
                return n;
            }
        }

        if (n == null)
        {
            // send HTTP response 404
        }
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