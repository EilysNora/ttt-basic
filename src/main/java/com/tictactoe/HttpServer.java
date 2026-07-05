package com.tictactoe;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;

/**
 * A stateless HTTP server for the tic-tac-toe game: every move is a plain
 * POST /move request carrying the whole board state as a query parameter.
 */
public class HttpServer {
    private static final int PORT = 8000;
    private static final int HUMAN_SYMBOL = 1;
    private static final String HUMAN_NAME = "Player#1";

    /**
     * Main method to start the HTTP server and register the /move endpoint.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        com.sun.net.httpserver.HttpServer server =
                com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/move", HttpServer::handle);
        server.setExecutor(null); // no executor => requests are handled one at a time on a single thread
        server.start();
        System.out.println("HTTP Server started on port " + PORT);
    }

    /**
     * Handles one /move request: rebuild the board, apply the move, check and then reply.
     * @param exchange
     * @throws IOException
     */
    private static void handle(HttpExchange exchange) throws IOException {
        // read the request: query params carry the board state and the move
        String state = param(exchange, "state");
        String move = param(exchange, "move");

        // rebuild the board from the client's state string (no state kept on the server itself)
        Board board = GameConfig.createBoard();
        if (!state.isEmpty() && !state.equalsIgnoreCase("NEW")) board.loadState(state);
        Player computer = GameConfig.createPlayer2();
        StringBuilder out = new StringBuilder();

        // check if the client wants to quit
        if (move.equalsIgnoreCase("quit")) {
            out.append("Bye!\n");
        } else if (!isNumber(move) || !board.isValidMove(Integer.parseInt(move))) {
            // not a number, or the cell is out of range / already taken
            out.append(move).append(" not available\n").append(HUMAN_NAME).append(" enter cell: ");
        } else {
            // place player move, then check winner, continue if not
            int cell = Integer.parseInt(move);
            board.place(cell, HUMAN_SYMBOL);
            out.append("  ").append(HUMAN_NAME).append(" turn at cell ").append(cell).append(".\n");
            show(out, board);
            if (!end(out, board, HUMAN_NAME)) {
                // computer's turn
                int comp = computer.chooseCell(board);
                board.place(comp, computer.getSymbol());
                out.append("  ").append(computer.getName()).append(" turn at cell ").append(comp).append(".\n");
                show(out, board);
                if (!end(out, board, computer.getName())) out.append(HUMAN_NAME).append(" enter cell: ");
            }
        }

        // reply: line 1 = new board state for the client to resend next time, rest = text to print
        byte[] body = (board.networkString() + "\n" + out).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
    }

    // pulls a single "key=value" pair out of the request's raw query string
    private static String param(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null) return "";
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(key)) return pair.substring(eq + 1);
        }
        return "";
    }

    // true if s can be parsed as an integer (used to validate the player's raw input)
    private static boolean isNumber(String s) {
        try { Integer.parseInt(s); return true; } catch (NumberFormatException e) { return false; }
    }

    // appends a win/draw message if the game just ended; returns true when it did
    private static boolean end(StringBuilder out, Board board, String who) {
        if (board.isWon()) { out.append(who).append(" wins!\n"); return true; }
        if (board.isFull()) { out.append("It's a draw!\n"); return true; }
        return false;
    }

    // renders the board to text and appends it to the reply
    private static void show(StringBuilder out, Board board) {
        StringWriter sw = new StringWriter();
        board.display(new PrintWriter(sw, true));
        out.append(sw);
    }
}
