package com.tictactoe;
import java.io.PrintWriter;

public class Squareboard implements Board {
    public static int EMPTY = 0;
    private int n;
    private int[] cells;
    
    public Squareboard(int n){
        this.n = n;
        this.cells = new int [n*n + 1];
    }

    public int getSize(){
        return n*n;
    }
    public int getCell(int cell){
        return cells[cell];
    }
    public boolean isValidMove(int cell){
        return cell>=1 && cell<=n*n && cells[cell] == EMPTY;
    }
    public void place(int cell, int PlayerSymbol){
        cells[cell] = PlayerSymbol;
    }
    public boolean isFull(){
        for (int i = 1; i<=n*n; i++)
            if (cells[i]==EMPTY) return false;
        return true;
    }

    public String networkString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n*n; i++){
            if (i > 1) sb.append(",");
            sb.append(cells[i]);
        }
        return sb.toString();
    }

    public void loadState(String state){
        String[] parts = state.split(",");
        for (int i = 0; i < parts.length && i < n*n; i++)
            cells[i + 1] = Integer.parseInt(parts[i].trim());
    }


    //convert 2D to 1D array
    private int cellOf(int row, int col){
        return row*n + col + 1;
    }

    //logic check row, col, diag and antidiag
    private boolean checkRow(int row){
        int firstcell = cells[cellOf(row, 0)];
        if (firstcell == EMPTY) return false;
        for (int col = 1; col < n; col++)
            if (cells[cellOf(row, col)]!=firstcell) return false;
        return true;
    }
    private boolean checkCol(int col){
        int firstcell = cells[cellOf(0, col)];
        if (firstcell == EMPTY) return false;
        for (int row = 1; row < n; row++)
            if(cells[cellOf(row, col)]!=firstcell) return false;
        return true;
    }
    private boolean checkMainDiag(){
        int firstcell = cells[cellOf(0, 0)];
        if (firstcell == EMPTY) return false;
        for (int i = 1; i < n ; i++)
            if(cells[cellOf(i, i)]!=firstcell) return false;
        return true;
    }
    private boolean checkAntiDiag(){
        int firstcell = cells[cellOf(0, n - 1)];
        if (firstcell == EMPTY) return false;
        for (int i = 1; i < n; i ++)
            if(cells[cellOf(i,n-1-i)]!=firstcell) return false;
        return true;
    }

    //from logic game to check won
    public boolean isWon(){
        for (int i = 0; i <n; i++){
            if (checkRow(i)) return true;
            if (checkCol(i)) return true;
        }
        if (checkMainDiag()) return true;
        if (checkAntiDiag()) return true;
        return false;
    }


    //  display the chess 0|0|0 x 3
    public void display (PrintWriter out){
        out.println();
        for (int row = 0; row <n; row++){
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col<n; col++){
                int cell = row *n +col+1;
                sb.append(String.format("%d", cells[cell]));
                if (col < n -1 ) sb.append("|");
            }
        out.println(sb);
        if (row < n-1) out.println("-".repeat(n*4 -1));
        }
        out.println();
    }


}