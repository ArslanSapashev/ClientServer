package com.sapashev.ClientHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Uploads file from client to server.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Uploader implements Handler {
    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        if(request.trim().matches("^upload\\s.+")){
            String[] args = request.trim().split("[ ]+");
            Path file = Paths.get(args[1]);
            if(Files.exists(file)){
                String requestToServer = String.format("upload %s %d %s", args[1], Files.size(file), args[2]);
                sendString(s.out(), requestToServer);
                if(receiveString(s).equals("READY")){
                    sendFile(s, file);
                }
                System.out.println(receiveString(s));
            }
        }
    }
}
