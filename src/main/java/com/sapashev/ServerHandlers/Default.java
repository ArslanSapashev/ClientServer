package com.sapashev.ServerHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;

/**
 * Default handler used when no any command has been recognized.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Default implements Handler {
    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        sendString(s.out(), "Server: Command has not been recognized");
    }
}
