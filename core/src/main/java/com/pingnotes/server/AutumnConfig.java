package com.pingnotes.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


/**
 * Created by shaobo on 11/3/16.
 */
public class AutumnConfig {
    private static final Timer timer = new Timer();
    private final static String conf = "autumn.properties";
    private static URL fileUrl = ClassLoader.getSystemResource(conf);
    private static long lastModified = 0;
    private static final Properties prop = new Properties();

    public static final String registryAddr = "registryAddress";
    public static final String scanPackage = "scanPackage";
    public static final String serverPort = "serverPort";

    static {
        load();
    }


    public static void load() {
        final URI uri;
        try {
            uri = fileUrl.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        {
            File file = new File(uri);
            try {
                prop.clear();
                prop.load(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    File file = new File(uri);
                    if (file.lastModified() != lastModified) {
                        prop.clear();
                        prop.load(new FileInputStream(file));
                        lastModified = file.lastModified();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    public static String getServerPort() {
        return (String) prop.getOrDefault(serverPort, "");
    }

    public static String getScanPackage() {
        return (String) prop.getOrDefault(scanPackage, "");
    }

    public static String getRegistryAddress() {
        return (String) prop.getOrDefault(registryAddr, "");
    }


    public static String getProperty(String key) {
        return (String) prop.getOrDefault(key, "");
    }

    public static List<String> allKeys() {
        return new ArrayList<>((Set<String>) (Set<?>) prop.keySet());
    }
}
