package org.example;

import java.util.List;

public class Message {
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

    public String getType() {
        return type;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getBoard() {
        return board;
    }

    public Integer getPlayerMaxChain() {
        return playerMaxChain;
    }

    public Integer getComputerMaxChain() {
        return computerMaxChain;
    }

    public String getResult() {
        return result;
    }


    public static Message raw(
            String type,
            Integer size,
            Integer row,
            Integer col,
            String status,
            String messageText,
            List<String> board,
            Integer playerMaxChain,
            Integer computerMaxChain,
            String result
    ) {
        Message message = new Message();
        message.type = type;
        message.size = size;
        message.row = row;
        message.col = col;
        message.status = status;
        message.message = messageText;
        message.board = board;
        message.playerMaxChain = playerMaxChain;
        message.computerMaxChain = computerMaxChain;
        message.result = result;
        return message;
    }

    public static Message initRequest(int size) {
        Message message = new Message();
        message.type = "init";
        message.size = size;
        return message;
    }

    public static Message moveRequest(int row, int col) {
        Message message = new Message();
        message.type = "move";
        message.row = row;
        message.col = col;
        return message;
    }

    public static Message playAgainRequest(boolean playAgain) {
        Message message = new Message();
        message.type = "restart";
        message.message = playAgain ? "yes" : "no";
        return message;
    }

    public static Message response(
            String status,
            String messageText,
            List<String> board,
            Integer playerMaxChain,
            Integer computerMaxChain,
            String result
    ) {
        Message message = new Message();
        message.type = "state";
        message.status = status;
        message.message = messageText;
        message.board = board;
        message.playerMaxChain = playerMaxChain;
        message.computerMaxChain = computerMaxChain;
        message.result = result;
        return message;
    }
}
