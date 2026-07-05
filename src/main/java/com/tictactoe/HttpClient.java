package com.tictactoe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

/**
 * A stateless HTTP client for the tic-tac-toe game: sends one POST /move
 * request per move, carrying the last known board state along with it.
 */
public class HttpClient {
    private static final String URL = "http://localhost:8000/move?state=%s&move=%s";

    /**
     * Main method to read player moves and POST them to the server, one request per move.
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        Scanner keyboard = new Scanner(System.in);
        String state = "NEW"; // no board yet, tell the server to start a fresh game
        System.out.print("Welcome to TicTacToe!\nPlayer#1 enter cell: ");

        while (keyboard.hasNextLine()) {
            String input = keyboard.nextLine().trim();

            // build and send the request: state + move go straight into the URL's query string
            HttpRequest request = HttpRequest.newBuilder(URI.create(String.format(URL, state, input)))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

            // line 1 of the reply = new board state, save it for the next move
            String[] parts = body.split("\n", 2);
            state = parts[0];

            // print the rest of the reply and watch for an end-of-game message
            boolean over = false;
            if (parts.length > 1) {
                for (String line : parts[1].split("\n")) {
                    System.out.print(line.endsWith(": ") ? line : line + "\n");
                    if (line.endsWith("wins!") || line.equals("It's a draw!") || line.equals("Bye!")) over = true;
                }
            }
            if (over || input.equalsIgnoreCase("quit")) break;
        }
    }
}
