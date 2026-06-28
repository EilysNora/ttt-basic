package com.tictactoe;

import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        int startingPlayer = parseArgument(args);
        Board board = GameConfig.createBoard();
        PrintWriter out = new PrintWriter(System.out, true);
        Player p1 = GameConfig.createPlayer1(null, out);
        Player p2 = GameConfig.createPlayer2();

        Game game;
        if (startingPlayer == 1) {
            game = new Game(board, p1, p2, out);
        } else {
            game = new Game(board, p2, p1, out);
        }
        game.run();
    }

    private static int parseArgument(String[] args) {
        if (args.length == 1) {
            try {
                int value = Integer.parseInt(args[0]);
                if (value == 1 || value == 2) return value;
            } catch (NumberFormatException ignored) {}
        }
        System.err.println("Usage: Main <1|2>  (1 = human first, 2 = computer first)");
        System.exit(1);
        return 0;
    }
}