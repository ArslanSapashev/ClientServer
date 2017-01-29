package com.sapashev;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Stores settings specific to particular socket.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class SocketSettings {
    private final Socket socket;
    private final String root;
    private final InputStream in;
    private final OutputStream out;
    private String dir;
    private int bufferSize;

    public SocketSettings(Socket socket, String root, InputStream in, OutputStream out){
        this.socket = socket;
        this.root = root;
        this.in = in;
        this.out = out;
    }

    public Socket socket(){
        return this.socket;
    }
    public InputStream in(){
        return this.in;
    }

    public OutputStream out(){
        return this.out;
    }

    public String dir(){
        return this.dir;
    }

    public String root(){
        return this.root;
    }

    public int bufferSize(){
        return this.bufferSize;
    }

    public void setDir(String dir){
        this.dir = dir;
    }

    public void setBufferSize (int size){
        this.bufferSize = size;
    }
}
