package com.sapashev.ServerHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sony on 28.01.2017.
 */
public class ChangeDir implements Handler {

    @Override
    public void handle (SocketSettings s, String request) throws IOException {
        boolean isChanged = false;
        if(request.matches("^goto\\s[\\.]{2,2}")){
            isChanged = gotoParent(s);
        }
        if(request.matches("^goto\\s[A-Za-z0-9\\\\/]+")){
           isChanged = goToSubDir(s, request.split("[ ]+")[1]);
        }
        String result = isChanged ? String.format("Changed to %s", s.dir()) : "No such directory";
        sendString(s.out(), result);
    }

    /**
     * Changes current directory to the specified subdirectory.
     * @param s - subdirectory to change on.
     * @return
     */
    private boolean goToSubDir (SocketSettings ss, String s) {
        boolean isChangedToSubDir = false;
        String argument = s;
        Path newDir = Paths.get(ss.dir(), argument);
        if(newDir.toFile().isDirectory()){
            ss.setDir(newDir.toAbsolutePath().toString());
            isChangedToSubDir = true;
        }
        return isChangedToSubDir;
    }

    /**
     * Goes to parent directory of current directory if it not the root.
     * @return - true - changed to parent, otherwise - false;
     */
    private boolean gotoParent(SocketSettings ss){
        boolean isChangedToParent = false;
        Path parent = Paths.get(ss.dir()).getParent();
        if(!ss.dir().equals(ss.root()) && parent != null){
            ss.setDir(parent.toAbsolutePath().toString());
            isChangedToParent = true;
        }
        return isChangedToParent;
    }


}
