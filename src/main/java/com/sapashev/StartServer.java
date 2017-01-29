package com.sapashev;

import java.io.IOException;

/**
 * Created by Sony on 28.01.2017.
 */
public class StartServer {
    public static void main (String[] args) {
        try {
            new Server().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
