package org.example.client;

import org.example.shared.Message;
import org.example.shared.SocketJsonIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements Closeable {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public ClientConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = SocketJsonIO.createReader(socket.getInputStream());
        this.writer = SocketJsonIO.createWriter(socket.getOutputStream());
    }

    public synchronized Message request(Message request) throws IOException {
        SocketJsonIO.writeMessage(writer, request);
        Message response = SocketJsonIO.readMessage(reader);
        if (response == null) {
            throw new IOException("Сервер закрыл соединение.");
        }
        return response;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
