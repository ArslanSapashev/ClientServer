package com.sapashev.ClientHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.*;

/**
 * Downloads file from server.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Downloader implements Handler{
    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        sendString(s.out(), request);
        long fileSize = Long.parseLong(receiveString(s));
        if(fileSize == 0){
            System.out.println("File not found");
            return;
        }
        String file = request.split("[ ]+")[2];
        sendString(s.out(), "READY");
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){
            boolean isAllBytesAccepted = receiveBytes(s, bos, fileSize);
            bos.flush();
            if(isAllBytesAccepted){
                System.out.println("File downloaded successfully");
            }
        }
    }
}
