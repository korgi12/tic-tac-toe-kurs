package org.example.shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonCodec {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private JsonCodec() {
    }

    public static String toJson(Message message) {
        return GSON.toJson(message);
    }

    public static Message fromJson(String json) {
        try {
            return GSON.fromJson(json, Message.class);
        } catch (Exception exception) {
            return null;
        }
    }
}
