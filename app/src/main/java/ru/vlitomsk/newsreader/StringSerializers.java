package ru.vlitomsk.newsreader;

/**
 * Created by v on 26.04.17.
 */
import android.util.Base64;
import java.io.*;

public class StringSerializers {

    /** Read the object from Base64 string. */
    public static Object fromString( String s ) {
        try {
            byte[] data = Base64.decode(s, 0);
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Write the object to a Base64 string. */
    public static String toString( Serializable o ) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            String s = Base64.encodeToString(baos.toByteArray(), 0);
            return s;
        } catch (Exception e) {
            return null;
    }
}
}
