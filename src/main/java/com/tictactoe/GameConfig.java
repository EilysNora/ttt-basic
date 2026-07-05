package com.tictactoe;

import java.io.*;

// Central place that builds the board and the two players shared by every server/client variant.
public class GameConfig {
    // always a classic 3x3 board
    public static Board createBoard() {
        return new Squareboard(3);
    }

    // the human player; falls back to the console when no streams are supplied
    public static Player createPlayer1(BufferedReader in, PrintWriter out) {
        if (in== null) in = new BufferedReader(new InputStreamReader(System.in));
        if (out == null) out = new PrintWriter(System.out, true);
        return new Humanplayer("Player#1", 1, in, out);
    }

    // the computer opponent
    public static Player createPlayer2() {
        return new Computerplayer("Computer", 2);
    }
}