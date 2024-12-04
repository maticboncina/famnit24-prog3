package org.vaje8;

import java.io.IOException;
import java.util.UUID;

public class Message {
    public final String id;
    public final MessageType type;
    public final String body;

    public Message(String rawMessage) throws IOException {
        String[] tokens = rawMessage.split(" ", 3);

        if (tokens.length != 3) {
            throw new IOException("Invalid message");
        }

        this.id = tokens[0];

        try {
            this.type = MessageType.valueOf(tokens[1]);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid message type");
        }

        this.body = tokens[2];
    }

    public Message(MessageType type, String body) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.body = body;
    }

    public String toString() {
        return id + " " + type + " " + body;
    }
}
