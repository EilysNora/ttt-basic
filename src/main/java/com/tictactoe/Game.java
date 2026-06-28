package com.tictactoe;
import java.io.PrintWriter;

public class Game {
    private Board board;
    private Player[] players;
    private int currentIndex;
    private PrintWriter out;

    public Game(Board board, Player first, Player second, PrintWriter out){
        this.board = board;
        this.players = new Player[]{first,second};
        this.currentIndex = 0;
        this.out = out;
    }

    public void run() {
        while (true) {
            Player current = players[currentIndex];
            int cell = current.chooseCell(board);
            board.place(cell, current.getSymbol());
            out.println("  " + current.getName() + " turn at cell " + cell + ".");
            board.display(out);

            if (board.isWon()) {
                out.println(current.getName() + " wins!");
                return;
            }
            if (board.isFull()) {
                out.println("It's a draw!");
                return;
            }
            currentIndex = 1 - currentIndex;
        }
    }
}