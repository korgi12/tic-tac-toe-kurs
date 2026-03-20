package org.example;

import java.util.List;

public record MoveResult(
        String status,
        String message,
        List<String> board,
        Integer playerMaxChain,
        Integer computerMaxChain,
        String result
) {
    public static MoveResult ok(
            String message,
            List<String> board,
            Integer playerMaxChain,
            Integer computerMaxChain,
            String result
    ) {
        return new MoveResult("ok", message, board, playerMaxChain, computerMaxChain, result);
    }

    public static MoveResult error(
            String message,
            List<String> board,
            Integer playerMaxChain,
            Integer computerMaxChain,
            String result
    ) {
        return new MoveResult("error", message, board, playerMaxChain, computerMaxChain, result);
    }

    public static MoveResult finished(
            String message,
            List<String> board,
            Integer playerMaxChain,
            Integer computerMaxChain,
            String result
    ) {
        return new MoveResult("finished", message, board, playerMaxChain, computerMaxChain, result);
    }
}
