package com.tictactoe;

public interface Player {
    String getName();
    int    getSymbol();
    int    chooseCell(Board board);
}