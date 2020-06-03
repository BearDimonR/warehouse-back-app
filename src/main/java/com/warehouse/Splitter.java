package com.warehouse;

import java.net.URI;

public class Splitter {

    public static long getId(URI uri) throws IllegalArgumentException  {
        String path = uri.getPath();
        return Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
    }
}