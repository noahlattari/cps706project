import java.util.Random;
import java.lang.Math;

public class Generate
{
    public int generateID(int num_nodes)
    {
        Random rand = new Random();
        int id = rand.nextInt(Math.pow(2, num_nodes));

        return id;
    }





}