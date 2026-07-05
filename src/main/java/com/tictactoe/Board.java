package com.tictactoe;

import java.io.PrintWriter;

public interface Board {
    int  getSize();
    int  getCell(int cell);
    void place(int cell, int playerSymbol);
    boolean isValidMove(int cell);
    boolean isWon();
    boolean isFull();
    void display(PrintWriter out);

    /** Serializes the board cells to a comma-separated string for network transfer. */
    String networkString();
    /** Restores board cells previously produced by {@link #networkString()}. */
    void loadState(String state);
}