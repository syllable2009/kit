package com.jxp.tinystruct.service;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 17:45
 */
public class Settings implements Configuration<String> {
    private static final long serialVersionUID = 8348657988449703373L;
    private static final String DEFAULT_FILE = "application.properties";
    private final Properties properties;
    private final String file;

    public Settings() {
        this(DEFAULT_FILE);
    }

    public Settings(String file) {
        this.file = file;
        this.properties = SingletonHolder.INSTANCE.getProperties(file);
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String get(String property) {
        String value = properties.getProperty(property);

        if (value != null && value.startsWith("$_")) {
            String envVariableName = value.substring(2).toUpperCase();
            return System.getenv(envVariableName) != null ? System.getenv(envVariableName) : "";
        }

        try {
            if (value != null) {
                byte[] bytes = value.getBytes(StandardCharsets.ISO_8859_1);
                return new String(bytes, StandardCharsets.UTF_8).trim();
            }
        } catch (Exception ignored) {
            // Ignored intentionally
        }

        return "";
    }

    @Override
    public void set(String key, String value) {
        properties.put(key, value);
        saveProperties();
    }

    @Override
    public void remove(String key) {
        properties.remove(key);
        saveProperties();
    }

    @Override
    public Set<String> propertyNames() {
        return properties.stringPropertyNames();
    }

    @Override
    public String getOrDefault(String key, String value) {
        return this.get(key).isEmpty() ? value : this.get(key);
    }

    @Override
    public void setIfAbsent(String key, String value) {
        if (!properties.containsKey(key)) {
            this.set(key, value);
        }
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    public void saveProperties() {
        URL resource = Settings.class.getClassLoader().getResource(this.file != null ? this.file : DEFAULT_FILE);
        if (null != resource) {
            URI uri;
            try {
                uri = resource.toURI();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e.getMessage());
            }

            String path = uri.getPath();
            if (path != null) {
                try (FileOutputStream outputStream = new FileOutputStream(path); OutputStream out = new BufferedOutputStream(outputStream)) {
                    properties.store(out, "#tinystruct configuration");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("File not found:" + e.getMessage(), e);
                } catch (IOException e) {
                    throw new RuntimeException("Error saving properties: " + e.getMessage(), e);
                }
            }
        }
    }

    private static final class SingletonHolder {
        public static final SingletonHolder INSTANCE = new SingletonHolder();
        private final Properties properties = new Properties();

        private Properties getProperties(String fileName) {
            try (InputStream in = Settings.class.getClassLoader().getResourceAsStream(fileName)) {
                if (in != null) {
                    properties.load(in);
                } else {
                    Logger.getLogger(Settings.class.getName()).warning("No settings loaded.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Error loading properties: " + e.getMessage(), e);
            }
            return properties;
        }
    }
}
