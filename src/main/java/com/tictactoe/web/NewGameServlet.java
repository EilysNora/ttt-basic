package com.tictactoe.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/newgame")
public class NewGameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        GameState game = new GameState();
        req.getSession(true).setAttribute(GameState.SESSION_KEY, game);
        JsonUtil.sendJson(resp, 200, JsonUtil.stateJson(game));
    }
}
