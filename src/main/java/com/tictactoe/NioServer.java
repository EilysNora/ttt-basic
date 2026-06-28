package com.tictactoe;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.Iterator;

public class NioServer {
    private static final int PORT = 12345;
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIO Server (no threads) started on port " + PORT + "...");

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) accept(key, selector);
                else if (key.isReadable()) read(key);
            }
        }
    }

    private static void accept(SelectionKey key, Selector selector) throws IOException {
        SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
        client.configureBlocking(false);
        GameSession session = new GameSession();
        client.register(selector, SelectionKey.OP_READ, session);
        System.out.println("Player connected: " + client.getRemoteAddress());
        session.greet(client);
    }

    private static void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        GameSession session = (GameSession) key.attachment();
        ByteBuffer buf = ByteBuffer.allocate(256);
        try {
            int n = channel.read(buf);
            if (n == -1) { close(key); return; }
            buf.flip();
            session.receive(UTF8.decode(buf).toString(), channel);
        } catch (IOException e) {
            close(key);
        }
    }

    private static void close(SelectionKey key) {
        try { key.channel().close(); } catch (IOException ignored) {}
        key.cancel();
    }

    static class GameSession {
        private final Board board = GameConfig.createBoard();
        private final Player computer = GameConfig.createPlayer2();
        private final StringBuilder inputBuf = new StringBuilder();
        private static final int HUMAN_SYMBOL = 1;
        private static final String HUMAN_NAME = "Player#1";

        void greet(SocketChannel ch) throws IOException {
            send(ch, "Welcome to TicTacToe!\n");
            sendBoard(ch);
            send(ch, HUMAN_NAME + " enter cell: ");
        }

        void receive(String data, SocketChannel ch) throws IOException {
            inputBuf.append(data);
            String line;
            while ((line = nextLine()) != null) {
                processMove(line, ch);
            }
        }

        private String nextLine() {
            int idx = inputBuf.indexOf("\n");
            if (idx == -1) return null;
            String line = inputBuf.substring(0, idx).replace("\r", "").trim();
            inputBuf.delete(0, idx + 1);
            return line;
        }

        private void processMove(String input, SocketChannel ch) throws IOException {
            int cell;
            try {
                cell = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                send(ch, "Invalid input. Enter a number 1-" + board.getSize() + ".\n");
                send(ch, HUMAN_NAME + " enter cell: ");
                return;
            }

            if (!board.isValidMove(cell)) {
                send(ch, cell + " not available\n");
                send(ch, HUMAN_NAME + " enter cell: ");
                return;
            }

            board.place(cell, HUMAN_SYMBOL);
            send(ch, "  " + HUMAN_NAME + " turn at cell " + cell + ".\n");
            sendBoard(ch);
            if (checkEnd(ch, HUMAN_NAME)) return;

            int comp = computer.chooseCell(board);
            board.place(comp, computer.getSymbol());
            send(ch, "  " + computer.getName() + " turn at cell " + comp + ".\n");
            sendBoard(ch);
            if (checkEnd(ch, computer.getName())) return;

            send(ch, HUMAN_NAME + " enter cell: ");
        }

        private boolean checkEnd(SocketChannel ch, String lastPlayer) throws IOException {
            if (board.isWon()) {
                send(ch, lastPlayer + " wins!\n");
                ch.close();
                return true;
            }
            if (board.isFull()) {
                send(ch, "It's a draw!\n");
                ch.close();
                return true;
            }
            return false;
        }

        private void sendBoard(SocketChannel ch) throws IOException {
            StringWriter sw = new StringWriter();
            board.display(new PrintWriter(sw, true));
            send(ch, sw.toString());
        }

        private void send(SocketChannel ch, String msg) throws IOException {
            ch.write(UTF8.encode(msg));
        }
    }
}
