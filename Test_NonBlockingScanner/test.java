import java.util.*;
import java.io.*;

public class test {
    public static void main(String[] args) {
        String s = "";
        long end = System.currentTimeMillis()+ 60*20;
        InputStreamReader fileInputStream=new InputStreamReader(System.in);
        BufferedReader bufferedReader=new BufferedReader(fileInputStream);

        try {
            while((System.currentTimeMillis() < end)) {
                if (bufferedReader.ready()) {
                    s += bufferedReader.readLine();
                }
            }
            bufferedReader.close();
        }
        catch(java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
