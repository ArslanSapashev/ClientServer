package com.sapashev;

import java.io.IOException;

/**
 * Created by Sony on 28.01.2017.
 */
public class StartClient {
    public static void main (String[] args) {
        try {
            new Client().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
