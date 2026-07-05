package com.tictactoe;

// Represents one player (human or computer) taking turns on a Board.
public interface Player {
    String getName();               // display name used in game messages
    int    getSymbol();             // marker value placed on the board (e.g. 1 or 2)
    int    chooseCell(Board board); // picks the next cell to play on the given board
}