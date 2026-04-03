package org.example.server;

import java.util.List;

public record MoveResult(
        String status,
        String message,
        List<String> board,
        Integer playerMaxChain,
        Integer computerMaxChain,
        String result
) {
    public static MoveResult error(String message, List<String> board) {
        return new MoveResult("error", message, board, null, null, null);
    }

    public static MoveResult ok(String message, List<String> board) {
        return new MoveResult("ok", message, board, null, null, null);
    }

    public static MoveResult finished(
            String message,
            List<String> board,
            int playerMaxChain,
            int computerMaxChain,
            String result
    ) {
        return new MoveResult("finished", message, board, playerMaxChain, computerMaxChain, result);
    }
}
