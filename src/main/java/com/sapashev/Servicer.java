package com.sapashev;


import com.sapashev.ServerHandlers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Conducts user request processing on server-side.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Servicer implements Runnable {
    private final Socket socket;
    private final String propertyFile;

    public Servicer(Socket socket, String propertyFile){
        this.socket = socket;
        this.propertyFile = propertyFile;
    }

    @Override
    public void run () {
        try{
            processor();
        }
        catch (IOException e){
            //log here
        }
    }

    /**
     * Dispatches user requests to the corresponding handlers.
     * @throws IOException
     */
    private void processor () throws IOException {
        try(InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream()){
            SocketSettings ss = createSocketSettings(socket, in, out);
            Map<String, Handler> map = createCommandMap();
            while (true){
                String request = getRequestFromUser(ss, in);
                String[] args = request.trim().split("[ ]+");
                if(args.length >= 1 && args[0] != null){
                    Handler handler = map.getOrDefault(args[0], new Default());
                    handler.handle(ss, request);
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
        map.put("upload", new Downloader());
        map.put("download", new Uploader());
        return map;
    }

    /**
     * Initializes socket settings.
     * @param in - socket input stream.
     * @param out - socket output stream.
     * @return - new SocketSettings object.
     */
    private SocketSettings createSocketSettings (Socket socket, InputStream in, OutputStream out) throws IOException {
        String root = Paths.get(getProperty(propertyFile, "root")).toAbsolutePath().normalize().toString();
        SocketSettings ss = new SocketSettings(socket, root, in, out);
        ss.setDir(root);
        ss.setBufferSize(Integer.parseInt(getProperty(propertyFile, "ServerBufferSize")));
        return ss;
    }

    /**
     * Reads request from user.
     * @param in - socket input stream.
     * @return - bytes converted to String without trailing "\r\n".
     * @throws IOException
     */
    private String getRequestFromUser(SocketSettings s, InputStream in) throws IOException {
        byte[] buffer = new byte[s.bufferSize()];
        int readBytes = in.read(buffer);
        String request = "";
        if(readBytes > 0){
            request = new String(Arrays.copyOf(buffer, readBytes));
            request = request.endsWith("\r\n") ? request.substring(0, request.length() - 2) : "";
        }
        return request;
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
