package com.tictactoe;

// A very simple computer opponent: always plays the first free cell it finds.
public class Computerplayer implements Player {
    private final String name;
    private final int symbol;
    public Computerplayer(String name, int symbol) {
        this.name = name;
        this.symbol = symbol;
    }
    public String getName(){
        return name;
    }
    public int getSymbol(){
        return symbol;
    }
    public int chooseCell(Board board) {
        // scan cells in order and take the first one that is still empty
        for (int cell = 1; cell <= board.getSize(); cell++)
            if (board.isValidMove(cell)) return cell;
        throw new IllegalStateException("full"); // caller should check isFull() before calling this
    }
}