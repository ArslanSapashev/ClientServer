package com.sapashev.ServerHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Uploads file to server
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Downloader implements Handler {

    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        String response = "File download failed";
        if(request.matches("^upload\\s[A-Za-z0-9:./]+\\s[0-9]+\\s[A-Za-z0-9:./]+")){
            try {
                Path file = createFile(request, s);
                sendString(s.out(), "READY");
                response = download(request, file, s) ? "File uploaded successfully" : response;
            } catch (FileAlreadyExistsException e) {
                response = "File already exist";
            }
        }
        sendString(s.out(), response);
    }

    /**
     * Uploads file from client to server locally.
     * @param request - client request.
     * @param file - file to download.
     * @return - true - uploaded successfully, false - otherwise.
     * @throws IOException
     */
    private boolean download (String request, Path file, SocketSettings s) throws IOException {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file.toFile()))){
            long fileSize = Long.parseLong(request.trim().split("[ ]+")[2]);
            boolean isAllBytesAccepted = receiveBytes(s, bos, fileSize);
            bos.flush();
            return isAllBytesAccepted;
        }
    }

    /**
     * Creates file locally if it doesn't exist.
     * @param request - request from client.
     * @return - newly created file (Path);
     * @throws IOException
     */
    private Path createFile (String request, SocketSettings s) throws IOException {
        String[] args = request.trim().split("[ ]+");
        Path file = Paths.get(s.dir(), args[3]);
        return Files.createFile(file);
    }
}
