package com.tictactoe.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/state")
public class StateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        GameState game = session == null ? null : (GameState) session.getAttribute(GameState.SESSION_KEY);
        if (game == null) {
            JsonUtil.sendJson(resp, 404, JsonUtil.error("no active game"));
            return;
        }
        JsonUtil.sendJson(resp, 200, JsonUtil.stateJson(game));
    }
}
