package Communication.Common.Serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageSerializer<T> implements IMessageSerializer<T> {

    @SuppressWarnings("unchecked")
    private static <T> T convertInstanceOfObject(Object o) {
        try {
            return (T)o;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        T object = null;
        try {
            in = new ObjectInputStream(bis);

            object = convertInstanceOfObject(in.readObject());
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Exception caught: " + e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e) {
                System.out.println("Exception caught: " + e);
            }
        }
        return object;
    }

    @Override
    public byte[] serialize(T object) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            bytes = bos.toByteArray();
        }
        catch (final IOException e) {
            System.out.println("Exception caught: " + e);
        }
        finally {
            try {
                bos.close();
            } catch (final IOException e) {
                System.out.println("Exception caught: " + e);
            }
        }
        return bytes;
    }
    
}
