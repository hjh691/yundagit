package com.example.substationtemperature.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    public static byte[] read(InputStream inputStream){
        byte[] buffer = new byte[1024];
        int length = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            while ((length = inputStream.read( buffer,0,buffer.length)) != -1){
                baos.write(buffer,0,length);
                inputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
