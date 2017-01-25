package com.sapashev;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

/**
 * Client class
 * @author Arslan Sapashev
 * @since 25.01.2017
 * @version 1.0
 */
public class Client {
    private String propertyFile = "app.properties";

    public void start() throws IOException {
        Socket socket = createSocket(propertyFile);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        Scanner scanner;
        while (true){
            scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String request = scanner.nextLine();
                if(request.equals("list")){
                    out.write("list all\r\n".getBytes(StandardCharsets.UTF_8));
                    String[] strings = getList(in);
                    for(String s : strings){
                        System.out.println(s);
                    }
                }
                if(request.equals("goto ..")){
                    out.write(appendEOL("goto ..").getBytes());
                    byte[] buffer = new byte[1024];
                    int readBytes = in.read(buffer);
                    System.out.println(new String(Arrays.copyOfRange(buffer,0,readBytes)));
                }
            }
        }
    }

    private String[] getList (InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        String[] response;
        int readBytes;
        readBytes = in.read(buffer);
        byte[] totalBuffer = new byte[readBytes];
        System.arraycopy(buffer,0, totalBuffer,0, readBytes);
        response = new String(totalBuffer).split("[\r\n]+");
        return response;
    }

    /**
     * Creates client-side socket.
     * @param propertyFile - property file which stores configuration details.
     * @return - created client socket.
     * @throws IOException
     */
    private Socket createSocket(String propertyFile) throws IOException {
        Socket socket = new Socket();
        String host = getProperty(propertyFile, "host");
        int timeout = Integer.parseInt(getProperty(propertyFile, "timeout"));
        int port = Integer.parseInt(getProperty(propertyFile, "port"));
        SocketAddress address = new InetSocketAddress(host, port);
        socket.connect(address, timeout);
        return socket;
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
     * Appends end-of-line marker (CRLF) to the end of response string (according to RFC 7231)
     * @param s - response string to be appended with marker
     * @return response string with marker;
     */
    private String appendEOL (String s){
        return s + "\r\n";
    }


}
