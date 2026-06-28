package com.tictactoe;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server (unbounded threads) started on port 12345...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New player connected — starting game thread.");
            new Thread(() -> {
                try {
                    handleGame(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try { socket.close(); } catch (IOException ignored) {}
                }
            }).start();
        }
    }

    private static void handleGame(Socket socket) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Board board = GameConfig.createBoard();
        Player human = GameConfig.createPlayer1(in, out);
        Player computer = GameConfig.createPlayer2();
        new Game(board, human, computer, out).run();
    }
}