package com.homeaway.dropwizard.bundle.resilience4j;

import java.io.File;
import java.net.URI;
import java.net.URL;

import com.google.common.io.Resources;

public class ResourceTestUtil {

    /**
     * Method to translate resource string into URL
     *
     * @param resourcePath path to a resource
     * @return URL for the resource
     */
    private static File resource(String resourcePath) {
        try {
            URL resource = Resources.getResource(ResourceTestUtil.class, resourcePath);
            URI uri = resource.toURI();
            return new File(uri);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String resourceAbsolutePath(String resourcePath) {
        return resource(resourcePath).getAbsolutePath();
    }
}