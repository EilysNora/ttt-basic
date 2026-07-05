package com.tictactoe.web;

import com.tictactoe.Board;
import com.tictactoe.Player;
import org.json.JSONException;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/move")
public class MoveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        GameState game = session == null ? null : (GameState) session.getAttribute(GameState.SESSION_KEY);
        if (game == null) {
            JsonUtil.sendJson(resp, 404, JsonUtil.error("no active game, start a new game first"));
            return;
        }
        if (game.isOver()) {
            JsonUtil.sendJson(resp, 409, JsonUtil.error("game already finished"));
            return;
        }

        int cell;
        try {
            JSONObject body = new JSONObject(JsonUtil.readBody(req));
            cell = body.getInt("cell");
        } catch (JSONException e) {
            JsonUtil.sendJson(resp, 400, JsonUtil.error("request body must be JSON like {\"cell\": <1-9>}"));
            return;
        }

        Board board = game.board;
        if (!board.isValidMove(cell)) {
            JsonUtil.sendJson(resp, 400, JsonUtil.error("cell " + cell + " is not available"));
            return;
        }

        board.place(cell, GameState.HUMAN_SYMBOL);
        StringBuilder msg = new StringBuilder(GameState.HUMAN_NAME + " played cell " + cell + ".");

        if (board.isWon()) {
            game.status = "HUMAN_WINS";
            msg.append(" ").append(GameState.HUMAN_NAME).append(" wins!");
        } else if (board.isFull()) {
            game.status = "DRAW";
            msg.append(" It's a draw!");
        } else {
            Player computer = game.computer;
            int comp = computer.chooseCell(board);
            board.place(comp, computer.getSymbol());
            msg.append(" ").append(computer.getName()).append(" played cell ").append(comp).append(".");

            if (board.isWon()) {
                game.status = "COMPUTER_WINS";
                msg.append(" ").append(computer.getName()).append(" wins!");
            } else if (board.isFull()) {
                game.status = "DRAW";
                msg.append(" It's a draw!");
            } else {
                msg.append(" ").append(GameState.HUMAN_NAME).append(": choose a cell.");
            }
        }

        game.message = msg.toString();
        JsonUtil.sendJson(resp, 200, JsonUtil.stateJson(game));
    }
}
