package com.sapashev;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;

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
    }

    private Socket createSocket(String propertyFile) throws IOException {
        Socket socket = new Socket();
        String host = getProperty(propertyFile, "host");
        int timeout = Integer.parseInt(getProperty(propertyFile, "Client connection timeout"));
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


}
