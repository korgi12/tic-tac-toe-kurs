package org.example.server;

public class DatabaseConfig {
    public String url() {
        return read("postgres", "postgres", "jdbc:postgresql://localhost:5432/postgres");
    }

    public String user() {
        return read("postgres", "postgres", "postgres");
    }

    public String password() {
        return read("postgres", "postgres", "postgres");
    }

    private String read(String envName, String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }
}
