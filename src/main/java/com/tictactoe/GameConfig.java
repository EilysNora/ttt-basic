package com.tictactoe;

import java.io.*;

public class GameConfig {
    public static Board createBoard() {
        return new Squareboard(3);
    }
    public static Player createPlayer1(BufferedReader in, PrintWriter out) {
        if (in== null) in = new BufferedReader(new InputStreamReader(System.in));
        if (out == null) out = new PrintWriter(System.out, true);
        return new Humanplayer("Player#1", 1, in, out);
    }
    public static Player createPlayer2() {
        return new Computerplayer("Computer", 2);
    }
}