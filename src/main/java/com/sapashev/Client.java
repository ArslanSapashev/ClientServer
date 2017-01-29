package com.sapashev;

import com.sapashev.ClientHandlers.ChangeDir;
import com.sapashev.ClientHandlers.Downloader;
import com.sapashev.ClientHandlers.Lister;
import com.sapashev.ClientHandlers.Uploader;
import com.sapashev.ClientHandlers.Default;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Implements client side interaction.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */

public class Client {
    private final String propertyFile = "app.properties";

    public void start() throws IOException {
        Socket socket = createSocket(propertyFile);
        try(InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in)) {

            Map<String, Handler> map = createCommandMap();
            SocketSettings s = createSocketSettings(socket, in, out);

            while (scanner.hasNext()){
                String request = scanner.nextLine();
                if(request.trim().equalsIgnoreCase("exit")){
                    break;
                }
                String[] args = request.trim().split("[ ]+");
                if(args.length >= 1 && args[0] != null){
                    Handler handler = map.getOrDefault(args[0], new Default());
                    handler.handle(s, request);
                }
            }
        }
    }

    /**
     * Creates and fills up map which contains mappings command-handler.
     * @return - command map.
     */
    private Map<String, Handler> createCommandMap(){
        Map<String, Handler> map = new HashMap<>();
        map.put("list", new Lister());
        map.put("goto", new ChangeDir());
        map.put("upload", new Uploader());
        map.put("download", new Downloader());
        return map;
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
     * Initializes socket settings.
     * @param in - socket input stream.
     * @param out - socket output stream.
     * @return - new SocketSettings object.
     */
    private SocketSettings createSocketSettings (Socket socket, InputStream in, OutputStream out) throws IOException{
        String root = getProperty(propertyFile, "root");
        SocketSettings ss = new SocketSettings(socket, root, in, out);
        ss.setDir(getProperty(propertyFile,"userDir"));
        ss.setBufferSize(Integer.parseInt(getProperty(propertyFile, "bufferSize")));
        return ss;
    }
}
