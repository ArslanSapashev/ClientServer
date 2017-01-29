package com.sapashev.ServerHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sends file to client.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Uploader implements Handler {
    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        String argument = request.split("[ ]+")[1];
        try {
            Path file = Paths.get(s.root(), argument);
            if(Files.exists(file)){
                String response = String.valueOf(Files.size(file));
                sendString(s.out(), response);
                if(receiveString(s).equals("READY")){
                    sendFile(s, file);
                }
            } else {
                sendString(s.out(), "0");
            }
        } catch (InvalidPathException e) {
            sendString(s.out(),"0");
        }
    }
}
