package org.example.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public final class SocketJsonIO {
    private SocketJsonIO() {
    }

    public static BufferedReader createReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public static BufferedWriter createWriter(OutputStream outputStream) {
        return new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    public static Message readMessage(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        return JsonCodec.fromJson(line);
    }

    public static void writeMessage(BufferedWriter writer, Message message) throws IOException {
        writer.write(JsonCodec.toJson(message));
        writer.newLine();
        writer.flush();
    }
}
