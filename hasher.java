/**
 Purpose: to convert content's name into server ID
 to determine target node

 **/

public class Hasher
{
    public static int modHash(String content_name)
    {
        int x = 0;
        for (int i = 0; i < content_name.length(); i++)
        {
            char character = content_name.charAt(i);
            int ascii = (int) character;

            x += ascii;
        }

        int y = X % 4;

        return y;
    }
}