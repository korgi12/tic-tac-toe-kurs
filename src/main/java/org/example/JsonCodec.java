package org.example;

import java.util.ArrayList;
import java.util.List;

public final class JsonCodec {
    private JsonCodec() {
    }

    public static String toJson(Message message) {
        StringBuilder builder = new StringBuilder("{");
        appendField(builder, "type", message.getType());
        appendField(builder, "size", message.getSize());
        appendField(builder, "row", message.getRow());
        appendField(builder, "col", message.getCol());
        appendField(builder, "status", message.getStatus());
        appendField(builder, "message", message.getMessage());
        appendArrayField(builder, "board", message.getBoard());
        appendField(builder, "playerMaxChain", message.getPlayerMaxChain());
        appendField(builder, "computerMaxChain", message.getComputerMaxChain());
        appendField(builder, "result", message.getResult());
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append('}');
        return builder.toString();
    }

    public static Message fromJson(String json) {
        if (json == null) {
            return null;
        }
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return null;
        }

        MessageBuilder builder = new MessageBuilder();
        builder.type(readString(trimmed, "type"));
        builder.size(readInteger(trimmed, "size"));
        builder.row(readInteger(trimmed, "row"));
        builder.col(readInteger(trimmed, "col"));
        builder.status(readString(trimmed, "status"));
        builder.message(readString(trimmed, "message"));
        builder.board(readStringArray(trimmed, "board"));
        builder.playerMaxChain(readInteger(trimmed, "playerMaxChain"));
        builder.computerMaxChain(readInteger(trimmed, "computerMaxChain"));
        builder.result(readString(trimmed, "result"));
        return builder.build();
    }

    private static void appendField(StringBuilder builder, String name, String value) {
        if (value != null) {
            builder.append('"').append(name).append("\":\"").append(escape(value)).append("\",");
        }
    }

    private static void appendField(StringBuilder builder, String name, Integer value) {
        if (value != null) {
            builder.append('"').append(name).append("\":").append(value).append(',');
        }
    }

    private static void appendArrayField(StringBuilder builder, String name, List<String> values) {
        if (values == null) {
            return;
        }
        builder.append('"').append(name).append("\":[");
        for (String value : values) {
            builder.append('"').append(escape(value)).append("\",");
        }
        if (!values.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("],");
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String readString(String json, String key) {
        String pattern = '"' + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        StringBuilder value = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char current = json.charAt(i);
            if (escaped) {
                value.append(current);
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '"') {
                return value.toString();
            } else {
                value.append(current);
            }
        }
        return null;
    }

    private static Integer readInteger(String json, String key) {
        String pattern = '"' + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        return Integer.parseInt(json.substring(start, end));
    }

    private static List<String> readStringArray(String json, String key) {
        String pattern = '"' + key + "\":[";
        int start = json.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        int end = json.indexOf(']', start);
        if (end < 0) {
            return null;
        }
        String content = json.substring(start, end).trim();
        List<String> values = new ArrayList<>();
        if (content.isEmpty()) {
            return values;
        }
        String[] parts = content.split("\",\"");
        for (String part : parts) {
            String cleaned = part;
            if (cleaned.startsWith("\"")) {
                cleaned = cleaned.substring(1);
            }
            if (cleaned.endsWith("\"")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            values.add(cleaned.replace("\\\"", "\"").replace("\\\\", "\\"));
        }
        return values;
    }

    private static final class MessageBuilder {
        private String type;
        private Integer size;
        private Integer row;
        private Integer col;
        private String status;
        private String message;
        private List<String> board;
        private Integer playerMaxChain;
        private Integer computerMaxChain;
        private String result;

        private void type(String value) { type = value; }
        private void size(Integer value) { size = value; }
        private void row(Integer value) { row = value; }
        private void col(Integer value) { col = value; }
        private void status(String value) { status = value; }
        private void message(String value) { message = value; }
        private void board(List<String> value) { board = value; }
        private void playerMaxChain(Integer value) { playerMaxChain = value; }
        private void computerMaxChain(Integer value) { computerMaxChain = value; }
        private void result(String value) { result = value; }

        private Message build() {
            return Message.raw(type, size, row, col, status, message, board, playerMaxChain, computerMaxChain, result);
        }
    }
}
