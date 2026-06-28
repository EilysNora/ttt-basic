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
}