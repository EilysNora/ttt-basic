package com.tictactoe;

import java.io.*;
import java.net.*;
import java.util.*;

public class Humanplayer implements Player {
    private final String name;
    private final int symbol;
    private final Scanner scanner;
    private final PrintWriter out;
    private final PrintWriter printer;
    private final BufferedReader in;

    public Humanplayer(String name, int symbol, BufferedReader in, PrintWriter out) {
        this.name = name;
        this.symbol = symbol;
        this.in = in;
        this.out = out;
        this.printer = out;
        this.scanner = new Scanner(in);
    }

    public String getName(){
        return name;
    }
    
    public int getSymbol(){
        return symbol;
    }

    public int chooseCell(Board board) {
        while (true) {
            printer.print(name + " enter cell: ");
            printer.flush();
            try {
                int cell = Integer.parseInt(scanner.nextLine().trim());
                if (board.isValidMove(cell)) return cell;
                printer.println(cell + " not available");
                printer.flush();
            } catch (NumberFormatException e) {
                printer.println("Something was wrong!!!");
                printer.flush();
            }
        }
    }

}