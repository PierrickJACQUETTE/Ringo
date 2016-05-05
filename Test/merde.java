import java.io.*;
import java.nio.*;

public class merde {
    public static void main(String[] args) {
        long i = 254254254;
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(i);
        byte[] result = bb.array();
        System.out.println(new String(result));
        ByteBuffer wrapped = ByteBuffer.wrap(result).order(ByteOrder.LITTLE_ENDIAN);
        Long num = wrapped.getLong();
        System.out.println(num);
    }
}
