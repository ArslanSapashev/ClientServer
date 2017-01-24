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
        server = initialize();
        String dir = getProperty(propertyFile, "root");
        Socket socket = server.accept();
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        sendToClient(out, appendEOF("READY"));
        while (true){
            Scanner scanner = new Scanner(in);
            if(scanner.hasNext()){
                try {
                    processRequest(scanner.nextLine().trim(), dir, out);
                }
                catch (SecurityException e) {
                    String msg = "You have no permission to access that file/directory";
                    sendToClient(out, appendEOF(msg));
                }
            }
        }
    }

    private void processRequest(String request, String dir, OutputStream out) throws IOException{
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
        if("download".equals(command) && isFile(argument)){

        }
    }

    /**
     * Sends file to the client
     * @param out - outputstream
     * @param file - file to be transmitted
     * @throws IOException
     */
    private void sendFile(OutputStream out, String file) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        String size = String.valueOf(new File(file).length());
        out.write(appendEOF(size).getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[bufferSize];
        int readBytes;
        while ((readBytes = in.read(buffer)) != -1){
            if(readBytes == bufferSize){
                out.write(buffer);
            } else {
                out.write(Arrays.copyOf(buffer,readBytes));
            }
        }
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
    private ServerSocket initialize() throws IOException{
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
