package com.sapashev;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Server class
 * @author Arslan Sapashev
 * @since 24.01.2017
 * @version 1.0
 */
public class Server {
    private ServerSocket server;
    int bufferSize = 1024;
    String propertyFile = "resources/app.properties";

    public void start() throws IOException {
        server = createServer();
        String dir = getProperty(propertyFile, "root");
        Socket socket = server.accept();
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream() ){
             sendToClient(out, appendEOF("READY"));
             Scanner scanner = new Scanner(in);
             while (true){
                if(scanner.hasNext()){
                    try {
                        processRequest(scanner.nextLine().trim(), dir, out, in);
                    }
                    catch (SecurityException e) {
                        String msg = "You have no permission to access that file/directory";
                        sendToClient(out, appendEOF(msg));
                    }
                }
             }
        }
    }

    private void processRequest(String request, String dir, OutputStream out, InputStream in) throws IOException{
        String[] reqs = request.trim().split("[ ]+");
        String command = reqs[0].trim().toLowerCase();
        String argument = reqs[1].trim().toLowerCase();
        String response;

        if("list".equals(command)){
            response = isDirectory(dir) ? getDirList(dir) : String.format("%s is not directory", dir);
            sendToClient(out, appendEOF(response));
        }
        if("goto".equals(command) && "..".equals(argument)){
            dir = (new File(dir).getParent()) != null ? new File(dir).getParent() : dir;
            response = dir != null ?
                    String.format("Current directory %s", dir) :
                    String.format("%s has no parent directory", dir);
            sendToClient(out, appendEOF(response));
        }
        if("goto".equals(command) && isDirectory(argument)){
            dir = argument;
            sendToClient(out, appendEOF(String.format("Directory changed to %s", dir)));
        }
        if("download".equals(command)){
            if(isFile(argument)){
                sendFileToClient(out, argument);
            } else {
                sendToClient(out, appendEOF("No such file"));
            }
        }
        if("upload".equals(command)){
            boolean result = false;
            if(new File(argument).isFile()){
                result = getFileFromClient(in, argument);
            }
            if(result){
                sendToClient(out, appendEOF("File uploaded successfully"));
            } else {
                sendToClient(out, appendEOF("Upload failure"));
            }
        }
    }

    /**
     * Downloads file from client to the server.
     * @param in - InputStream bounded to the socket
     * @param filename - file to download 'n store locally.
     * @throws IOException
     */
    private boolean getFileFromClient(InputStream in, String filename, long filesize) throws IOException {
        boolean result = false;
        File file = new File(filename);
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
            if(file.createNewFile()){
                result = transferFile(in, out, filesize);
            }
        }
        return result;
    }

    /**
     * Sends file to the client. Before sending the file, file size will be sent to the client.
     * @param out - outputstream
     * @param file - file to be transmitted
     * @throws IOException
     */
    private boolean sendFileToClient (OutputStream out, String file) throws IOException {
        long fileSize = new File(file).length();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))){
            sendToClient(out, appendEOF(String.valueOf(fileSize)));
            return transferFile(in, out, fileSize);
        }
    }

    /**
     * Copys file from source to destination.
     * @param in - inputstream (socket or file).
     * @param out - outputstream (ocket or file).
     * @param filesize - size of file to be transferred.
     * @return - true - file copy completed, false - not completed due to some error or exception.
     * @throws IOException
     */
    private boolean transferFile (InputStream in, OutputStream out, long filesize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int readBytes;
        long alreadyRead = 0;
        while ((readBytes = in.read(buffer)) != -1){
            if(readBytes == bufferSize){
                out.write(buffer);
            } else {
                out.write(Arrays.copyOf(buffer,readBytes));
            }
            alreadyRead += readBytes;
        }
        return alreadyRead == filesize ? true : false;
    }

    /**
     * Checks if specified string refers to the file.
     * @param s - path to check.
     * @return - true - it is file, false - otherwise.
     */
    private boolean isFile(String s){
        return new File(s).isFile();
    }

    /**
     * Checks if the specified string refers to the subdirectory.
     * @param s - path to check
     * @return - true - it is directory, false - otherwise.
     */
    private boolean isDirectory(String s){
        return new File(s).isDirectory();
    }

    /**
     * Sends response to the client through the OutputStream of the socket.
     * @param s - response
     * @throws IOException
     */
    private void sendToClient (OutputStream out, String s) throws IOException{
        out.write(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Appends end-of-line marker (CRLF) to the end of response string (according to RFC 7231)
     * @param s - response string to be appended with marker
     * @return response string with marker;
     */
    private String appendEOF(String s){
        return s + "\r\n";
    }

    /**
     * Creates ServerSocket.
     * @return - ServerSocket
     * @throws IOException
     */
    private ServerSocket createServer () throws IOException{
        int port = Integer.parseInt(getProperty(propertyFile, "port"));
        return new ServerSocket(port);
    }

    /**
     * Returns specific property from property file.
     * @param propertyFile - property file
     * @param field - field to be retrieved
     * @return  - string representation of specified field
     * @throws IOException
     */
    private String getProperty (String propertyFile, String field) throws IOException{
        Properties p = new Properties();
        p.load(this.getClass().getClassLoader().getResourceAsStream(propertyFile));
        return p.getProperty(field);
    }

    /**
     * Returns list of files and subdirectories of that directory.
     * @param dir - directory which list should be retrieved.
     * @return - String
     */
    private String getDirList (String dir) throws IOException {
        try(Stream<Path> paths = Files.list(Paths.get(dir))){
            return paths.map((x) -> x.getFileName().toString()).collect(Collectors.joining("\r\n"));
        }
    }
}
