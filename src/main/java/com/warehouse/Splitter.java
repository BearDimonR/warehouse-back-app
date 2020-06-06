package com.warehouse;

import java.net.URI;

public class Splitter {

    public static long getId(URI uri) throws IllegalArgumentException  {
        String path = uri.getPath();
        String res = path.substring(path.lastIndexOf('/') + 1);
        try {
            return Long.valueOf(res);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
