package com.tictactoe;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpClient {
    private static final String BASE_URL = "http://localhost:8000";

    public static void main(String[] args) throws IOException, InterruptedException {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        Scanner keyboard = new Scanner(System.in);

        HttpResponse<String> newGame = send(client, BASE_URL + "/newgame");
        JSONObject state = new JSONObject(newGame.body());
        int id = state.getInt("id");
        printState(state);

        while ("IN_PROGRESS".equals(state.getString("status"))) {
            System.out.print("> ");
            if (!keyboard.hasNextLine()) break;
            Integer cell = parseInt(keyboard.nextLine().trim());
            if (cell == null) {
                System.out.println("Enter a number.");
                continue;
            }

            HttpResponse<String> response = send(client, BASE_URL + "/move?id=" + id + "&cell=" + cell);
            JSONObject body = new JSONObject(response.body());
            if (response.statusCode() >= 400) {
                System.out.println(body.optString("error", "Error " + response.statusCode()));
                continue;
            }
            state = body;
            printState(state);
        }
    }

    private static HttpResponse<String> send(java.net.http.HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void printState(JSONObject state) {
        printBoard(state.getJSONArray("board"), state.getInt("size"));
        System.out.println(state.getString("message"));
    }

    private static void printBoard(JSONArray board, int size) {
        int n = (int) Math.round(Math.sqrt(size));
        System.out.println();
        for (int row = 0; row < n; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < n; col++) {
                sb.append(board.getInt(row * n + col));
                if (col < n - 1) sb.append("|");
            }
            System.out.println(sb);
            if (row < n - 1) System.out.println("-".repeat(n * 4 - 1));
        }
        System.out.println();
    }

    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
