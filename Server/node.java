import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

// Implement Serializable
public class Node implements Serializable {
    private String id;
    private int port;
    private InetAddress ip;
    private Node child;
    private Node parent;
    private Client client;
    private Server server;

    private Map<String, List<Integer>> map;

    public Node(String id, InetAddress ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.client = new Client();
        this.server = new Server();
        this.map = new HashMap<>();
    }

    public void linkNodes(Node child, Node parent) {
        this.child = child;
        this.parent = parent;
    }

    public void setInetAddress(InetAddress addr)
    {
        this.setInetAddress = addr;
    }

    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(this.ip, this.port);
    }

    public void setMap(String key, int value)
    {
        List<Integer> values = this.map.get(key);
        if (values != null)
        {
            values.add(value);

        }
        else
        {
            List<Integer> values = new ArrayList<Integer>();
            values.add(value);
        }

        this.map.put(key, values);
    }

    public Map<String, List<Integer>> getMap(String key)
    {
        return this.map.get(key);
    }
}


