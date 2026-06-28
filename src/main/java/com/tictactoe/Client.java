package com.tictactoe;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        // Thread to print everything the server sends
        new Thread(() -> {
            try {
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = fromServer.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) { /* server closed */ }
        }).start();

        // Main thread sends everything the user types
        Scanner keyboard = new Scanner(System.in);
        PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
        while (keyboard.hasNextLine()) {
            toServer.println(keyboard.nextLine());
        }

        socket.close();
    }
}