package com.tictactoe.web;

import com.tictactoe.Board;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

final class JsonUtil {
    private JsonUtil() {}

    static JSONObject stateJson(GameState game) {
        JSONObject obj = new JSONObject();
        obj.put("board", boardArray(game.board));
        obj.put("size", game.board.getSize());
        obj.put("status", game.status);
        obj.put("message", game.message);
        return obj;
    }

    private static JSONArray boardArray(Board board) {
        JSONArray arr = new JSONArray();
        for (int cell = 1; cell <= board.getSize(); cell++) arr.put(board.getCell(cell));
        return arr;
    }

    static JSONObject error(String message) {
        return new JSONObject().put("error", message);
    }

    static void sendJson(HttpServletResponse resp, int statusCode, JSONObject body) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(body.toString());
    }

    static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            char[] buf = new char[1024];
            int read;
            while ((read = reader.read(buf)) != -1) sb.append(buf, 0, read);
        }
        return sb.toString();
    }
}
