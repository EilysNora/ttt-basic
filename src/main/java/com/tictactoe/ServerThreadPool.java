package com.tictactoe;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServerThreadPool {
    private static final int POOL_SIZE = 4;

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        System.out.println("Server (thread pool, " + POOL_SIZE + " threads) started on port 12345...");

        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New player connected — submitting to thread pool.");
                pool.submit(() -> {
                    try {
                        handleGame(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try { socket.close(); } catch (IOException ignored) {}
                    }
                });
            }
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
