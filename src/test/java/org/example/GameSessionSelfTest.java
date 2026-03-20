package org.example;

public class GameSessionSelfTest {
    public static void main(String[] args) {
        rejectsOccupiedCell();
        computesLongestChainFromBoard();
        jsonRoundTrip();
        System.out.println("Self-tests passed.");
    }

    private static void rejectsOccupiedCell() {
        GameSession session = new GameSession(3);
        MoveResult firstMove = session.playerMove(0, 0);
        MoveResult secondMove = session.playerMove(0, 0);

        assertEquals("ok", firstMove.status(), "first move status");
        assertEquals("error", secondMove.status(), "second move status");
        assertEquals("Клетка уже занята.", secondMove.message(), "occupied message");
    }

    private static void computesLongestChainFromBoard() {
        GameSession session = new GameSession(5);
        session.playerMove(0, 0);
        session.playerMove(1, 1);
        session.playerMove(2, 2);
        assertEquals(3, session.findLongestChain(GameSession.PLAYER), "player longest chain");
    }

    private static void jsonRoundTrip() {
        Message message = Message.response("finished", "done", java.util.List.of("XO.", ".OX"), 2, 2, "draw");
        Message parsed = JsonCodec.fromJson(JsonCodec.toJson(message));
        assertEquals("finished", parsed.getStatus(), "json status");
        assertEquals("done", parsed.getMessage(), "json message");
        assertEquals(2, parsed.getPlayerMaxChain(), "json player chain");
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            throw new IllegalStateException(label + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
