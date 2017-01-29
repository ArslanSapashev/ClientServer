package com.sapashev;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Handles user request
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public interface Handler {
    void handle(SocketSettings s, String request) throws IOException ;

    /**
     * Appends end-of-line marker (CRLF) to the end of response string (according to RFC 7231)
     * @param s - response string to be appended with marker
     * @return response string with marker;
     */
    default String appendEOL (String s){
        return s + "\r\n";
    }

    /**
     * Sends response to the client through the OutputStream of the socket.
     * @param s - response
     * @throws IOException
     */
    default void sendString (OutputStream out, String s) throws IOException{
        out.write(appendEOL(s).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sends file to client by bytes.
     * @param s - socket settings
     * @param file - file to send.
     * @throws IOException
     */
    default void sendFile (SocketSettings s, Path file) throws IOException {
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.toFile()))){
            int readBytes = 0;
            long totalBytes = 0;
            byte[] buffer = new byte[s.bufferSize()];
            while ((readBytes = bis.read(buffer)) != -1 && totalBytes < Files.size(file)){
                s.out().write(Arrays.copyOf(buffer,readBytes));
            }
        }
    }

    /**
     * Receives request from another side, converts it to string and erases last 2 symbols (\r\n).
     * It supposes that incoming request encoded using UTF-8 charset.
     * @param s - socket settings
     * @return - request without trailing end-of-line (\r\n) symbols.
     * @throws IOException
     */
    default String receiveString(SocketSettings s) throws IOException {
        int readBytes = 0;
        byte[] buffer = new byte[s.bufferSize()];
        readBytes = s.in().read(buffer);
        String response = new String(Arrays.copyOf(buffer, readBytes), StandardCharsets.UTF_8);
        if(response.endsWith("\r\n")){
            response = response.substring(0, response.length() - 2);
        }
        return response;
    }

    /**
     * Receives specified amount of bytes from input stream and saves it to the output stream.
     * @param s - socket settings
     * @param out - output stream to which save accepted bytes.
     * @param bytesToRead - amount of bytes to read from input stream.
     * @return
     * @throws IOException
     */
    default boolean receiveBytes(SocketSettings s, OutputStream out, long bytesToRead) throws IOException {
        long acceptedBytes = 0;
        int readBytes = 0;
        byte[] buffer = new byte[s.bufferSize()];
        while (acceptedBytes < bytesToRead && (readBytes = s.in().read(buffer)) != -1){
            out.write(Arrays.copyOf(buffer, readBytes));
            acceptedBytes = acceptedBytes + readBytes;
        }
        return acceptedBytes == bytesToRead;
    }
}
