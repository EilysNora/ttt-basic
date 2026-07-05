package com.tictactoe;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A stateless client for the tic-tac-toe game: opens a brand-new connection
 * for every move, sending the last known board state along with it.
 */
public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    /**
     * Main method to read player moves and talk to the server, one move per connection.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        String state = "NEW"; // no board yet, tell the server to start a fresh game
        System.out.print("Welcome to TicTacToe!\nPlayer#1 enter cell: ");

        while (keyboard.hasNextLine()) {
            String input = keyboard.nextLine().trim();

            // send this move to the server and get the full reply back
            List<String> lines = request(state, input);
            if (lines.isEmpty()) break;
            state = lines.get(0); // save the new board state for the next move

            // print the rest of the reply and watch for an end-of-game message
            boolean over = false;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                System.out.print(line.endsWith(": ") ? line : line + "\n");
                if (line.endsWith("wins!") || line.equals("It's a draw!") || line.equals("Bye!")) over = true;
            }
            if (over || input.equalsIgnoreCase("quit")) break;
        }
    }

    /**
     * Sends one request (state + move) over a fresh socket and reads the full reply.
     * @param state
     * @param input
     * @throws IOException
     */
    private static List<String> request(String state, String input) throws IOException {
        try (Socket socket = new Socket(HOST, PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(state);
            out.println(input);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) lines.add(line);
            return lines;
            // at the end of the try block, the socket will be closed automatically
        }
    }
}
