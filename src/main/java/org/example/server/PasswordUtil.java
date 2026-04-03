package org.example.server;

import java.util.Objects;

public final class PasswordUtil {
    public static boolean verifyPassword(String password, String storedValue) {
        return Objects.equals(password, storedValue);
    }
}
