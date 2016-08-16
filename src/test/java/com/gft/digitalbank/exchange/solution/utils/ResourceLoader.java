package com.gft.digitalbank.exchange.solution.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Ivo on 15/08/16.
 */
public class ResourceLoader {

    public String readStringFromResourceFile(String path) throws IOException {
        URL url = Resources.getResource(path);
        return Resources.toString(url, Charsets.UTF_8);
    }
}