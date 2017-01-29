package com.sapashev;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Server {
    private final String propertyFile = "app.properties";

    public void start() throws IOException {
        ServerSocket server = createServer();
        ExecutorService service = Executors.newFixedThreadPool(4);
        while (true){
            Socket socket = server.accept();
            service.submit(new Servicer(socket, propertyFile));
        }
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
     * Creates ServerSocket.
     * @return - ServerSocket
     * @throws IOException
     */
    private ServerSocket createServer () throws IOException{
        int port = Integer.parseInt(getProperty(propertyFile, "port"));
        ServerSocket server = new ServerSocket(port);
        server.setReuseAddress(true);
        return server;
    }
}
