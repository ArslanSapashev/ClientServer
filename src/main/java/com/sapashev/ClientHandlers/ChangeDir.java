package com.sapashev.ClientHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;

/**
 * Sends to server change directory command and receives server response.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class ChangeDir implements Handler {
    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        sendString(s.out(), request);
        System.out.println(receiveString(s));
    }
}
