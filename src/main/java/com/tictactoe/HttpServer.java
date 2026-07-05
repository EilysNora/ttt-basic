package com.tictactoe;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpServer {
    private static final int PORT = 8000;
    private static final int HUMAN_SYMBOL = 1;
    private static final String HUMAN_NAME = "Player#1";

    public static void main(String[] args) throws IOException {
        Map<Integer, GameSession> games = new HashMap<>();
        AtomicInteger nextId = new AtomicInteger(1);

        com.sun.net.httpserver.HttpServer server =
                com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/newgame", exchange -> handleNewGame(exchange, games, nextId));
        server.createContext("/move", exchange -> handleMove(exchange, games));
        server.createContext("/board", exchange -> handleBoard(exchange, games));

        // No executor set => exchanges are dispatched one at a time on a single thread.
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Server (single-threaded) started on port " + PORT + "...");
    }

    private static void handleNewGame(HttpExchange exchange, Map<Integer, GameSession> games, AtomicInteger nextId) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, error("method not allowed"));
            return;
        }
        int id = nextId.getAndIncrement();
        GameSession session = new GameSession(GameConfig.createBoard(), GameConfig.createPlayer2());
        session.message = HUMAN_NAME + " enter cell:";
        games.put(id, session);
        sendJson(exchange, 200, sessionJson(id, session));
    }

    private static void handleMove(HttpExchange exchange, Map<Integer, GameSession> games) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, error("method not allowed"));
            return;
        }
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        Integer id = parseInt(params.get("id"));
        if (id == null || !games.containsKey(id)) {
            sendJson(exchange, 404, error("no such game"));
            return;
        }

        GameSession session = games.get(id);
        if (session.isOver()) {
            sendJson(exchange, 410, error("game already finished"));
            return;
        }

        Board board = session.board;
        Integer cell = parseInt(params.get("cell"));
        if (cell == null || !board.isValidMove(cell)) {
            sendJson(exchange, 400, error(params.get("cell") + " not available"));
            return;
        }

        board.place(cell, HUMAN_SYMBOL);
        StringBuilder msg = new StringBuilder("  " + HUMAN_NAME + " turn at cell " + cell + ".");

        if (board.isWon()) {
            session.status = "HUMAN_WINS";
            msg.append(" ").append(HUMAN_NAME).append(" wins!");
        } else if (board.isFull()) {
            session.status = "DRAW";
            msg.append(" It's a draw!");
        } else {
            Player computer = session.computer;
            int comp = computer.chooseCell(board);
            board.place(comp, computer.getSymbol());
            msg.append("  ").append(computer.getName()).append(" turn at cell ").append(comp).append(".");

            if (board.isWon()) {
                session.status = "COMPUTER_WINS";
                msg.append(" ").append(computer.getName()).append(" wins!");
            } else if (board.isFull()) {
                session.status = "DRAW";
                msg.append(" It's a draw!");
            } else {
                msg.append("  ").append(HUMAN_NAME).append(" enter cell:");
            }
        }

        session.message = msg.toString();
        sendJson(exchange, 200, sessionJson(id, session));
    }
    
    private static void handleBoard(HttpExchange exchange, Map<Integer, GameSession> games) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, error("method not allowed"));
            return;
        }
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        Integer id = parseInt(params.get("id"));
        if (id == null || !games.containsKey(id)) {
            sendJson(exchange, 404, error("no such game"));
            return;
        }
        sendJson(exchange, 200, sessionJson(id, games.get(id)));
    }

    private static JSONObject sessionJson(int id, GameSession session) {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("board", boardArray(session.board));
        obj.put("size", session.board.getSize());
        obj.put("status", session.status);
        obj.put("message", session.message);
        return obj;
    }

    private static JSONArray boardArray(Board board) {
        JSONArray arr = new JSONArray();
        for (int cell = 1; cell <= board.getSize(); cell++) arr.put(board.getCell(cell));
        return arr;
    }

    private static JSONObject error(String message) {
        return new JSONObject().put("error", message);
    }

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }
        return params;
    }

    private static Integer parseInt(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void sendJson(HttpExchange exchange, int statusCode, JSONObject body) throws IOException {
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static class GameSession {
        final Board board;
        final Player computer;
        String status = "IN_PROGRESS";
        String message;

        GameSession(Board board, Player computer) {
            this.board = board;
            this.computer = computer;
        }

        boolean isOver() {
            return !"IN_PROGRESS".equals(status);
        }
    }
}
