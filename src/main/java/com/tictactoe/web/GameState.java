package com.tictactoe.web;

import com.tictactoe.Board;
import com.tictactoe.GameConfig;
import com.tictactoe.Player;

public class GameState {
    public static final int HUMAN_SYMBOL = 1;
    public static final String HUMAN_NAME = "Player#1";
    static final String SESSION_KEY = "ttt.game";

    final Board board;
    final Player computer;
    String status = "IN_PROGRESS";
    String message;

    GameState() {
        this.board = GameConfig.createBoard();
        this.computer = GameConfig.createPlayer2();
        this.message = HUMAN_NAME + ": choose a cell (1-" + board.getSize() + ").";
    }

    boolean isOver() {
        return !"IN_PROGRESS".equals(status);
    }
}
