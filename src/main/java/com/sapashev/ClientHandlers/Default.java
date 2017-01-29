package com.sapashev.ClientHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;

/**
 * Used for default action when entered by user command hasn't been recognized.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Default implements Handler {
    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        System.out.println("Client: Command has not been recognized");
    }
}
