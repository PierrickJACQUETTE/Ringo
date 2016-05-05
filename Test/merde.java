import java.io.*;
import java.nio.*;
import java.nio.charset.*;

public class merde {
    public static void main(String[] args) {
        try {
            long i = 254254254;
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(i);
            byte[] result = bb.array();
            System.out.println(new String(result, "ISO-8859-1"));
            String a = new String(result, "ISO-8859-1");
            byte[] tmp = a.getBytes("ISO-8859-1");
            ByteBuffer wrapped = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN);
            long num = wrapped.getLong();
            System.out.println(num);
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
