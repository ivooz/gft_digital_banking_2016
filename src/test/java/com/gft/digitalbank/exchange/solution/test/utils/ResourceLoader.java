package com.gft.digitalbank.exchange.solution.test.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Ivo on 15/08/16.
 */
public class ResourceLoader {

    /**
     *
     * @param path in the resource directory
     * @return the stringified file
     * @throws IOException
     */
    public String readStringFromResourceFile(String path) throws IOException {
        URL url = Resources.getResource(path);
        return Resources.toString(url, Charsets.UTF_8);
    }
}