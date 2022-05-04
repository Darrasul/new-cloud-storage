package com.buzas.cloud.application.network;

import com.buzas.cloud.application.model.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class ClientNetwork {

    private final Socket socket;
    private final ObjectDecoderInputStream input;
    private final ObjectEncoderOutputStream output;

    private final String host;
    private final int port;

    public ClientNetwork() throws IOException {
        host = "localhost";
        port = 8189;
        socket = new Socket(host, port);
        output = new ObjectEncoderOutputStream(socket.getOutputStream());
        input = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public ClientNetwork(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        output = new ObjectEncoderOutputStream(socket.getOutputStream());
        input = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public void closeNetwork() throws IOException {
        this.socket.close();
        System.out.println("Client disconnected");
    }

    public AbstractMessage read() throws Exception {
        return (AbstractMessage) input.readObject();
    }

    public void write(AbstractMessage message) throws IOException {
        System.out.println("writing message: " + message);
        output.writeObject(message);
    }

    public void download(AbstractMessage message) throws IOException {
        System.out.println("receiving message: " + message);
        output.writeObject(message);
    }
}
