package com.tictactoe;

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
        for (int cell = 1; cell <= board.getSize(); cell++)
            if (board.isValidMove(cell)) return cell;
        throw new IllegalStateException("full");
    }
}