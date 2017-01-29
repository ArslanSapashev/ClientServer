package com.sapashev.ClientHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;
import java.util.Arrays;

/**
 * Prints list of files and directories.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Lister implements Handler {

    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        sendString(s.out(), "list");
        String[] files = receiveString(s).split(";");
        Arrays.stream(files).forEach(System.out::println);
    }
}
