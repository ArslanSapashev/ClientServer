package com.sapashev.ServerHandlers;

import com.sapashev.Handler;
import com.sapashev.SocketSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Returns list of files and directories.
 * @author Arslan Sapashev
 * @since 27.01.2017
 * @version 1.0
 */
public class Lister implements Handler {

    @Override
    public void handle (SocketSettings s, String dir) throws IOException {
        sendString(s.out(), getDirList(s.dir()));
    }

    /**
     * Returns list of files and subdirectories of that directory.
     * @param dir - directory which list should be retrieved.
     * @return - String
     */
    private String getDirList (String dir) throws IOException {
        try(Stream<Path> paths = Files.list(Paths.get(dir))){
            return paths.map((x) -> x.getFileName().toString()).collect(Collectors.joining(";"));
        }
    }
}
