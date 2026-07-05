package com.tictactoe;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.Iterator;

/**
 * A stateless NIO server for the tic-tac-toe game: every move is a brand-new
 * connection carrying the whole board state, processed and replied to, then closed.
 */
public class NioServer {
    private static final int PORT = 12345;
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int HUMAN_SYMBOL = 1;
    private static final String HUMAN_NAME = "Player#1";

    /**
     * Main method to start the server and listen for client connections.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // set up selector and non-blocking server channel
        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(PORT));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port " + PORT);

        // event loop: block until something is ready, then handle it
        while (true) {
            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    // a new client wants to connect
                    SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // a client sent its request; handle it and close (stateless, no keep-alive)
                    SocketChannel ch = (SocketChannel) key.channel();
                    try { handle(ch); } catch (IOException ignored) {}
                    ch.close();
                    key.cancel();
                }
            }
        }
    }

    /**
     * Handles one client request: rebuild the board, apply the move, check and then reply.
     * @param ch
     * @throws IOException
     */
    private static void handle(SocketChannel ch) throws IOException {
        // read the request: line 1 is the board state, line 2 is the move
        ByteBuffer buf = ByteBuffer.allocate(256);
        ch.read(buf);
        buf.flip();
        String[] lines = UTF8.decode(buf).toString().split("\n", 2);
        String state = lines[0].trim();
        String move = lines.length > 1 ? lines[1].trim() : "";

        // rebuild the board from the client's state string (no state kept on the server itself)
        Board board = GameConfig.createBoard();
        if (!state.isEmpty() && !state.equalsIgnoreCase("NEW")) board.loadState(state);
        Player computer = GameConfig.createPlayer2();
        StringBuilder out = new StringBuilder();

        // check if the client wants to quit
        if (move.equalsIgnoreCase("quit")) {
            out.append("Bye!\n");
        } else if (!isNumber(move) || !board.isValidMove(Integer.parseInt(move))) {
            // not a number, or the cell is out of range / already taken
            out.append(move).append(" not available\n").append(HUMAN_NAME).append(" enter cell: ");
        } else {
            // place player move, then check winner, continue if not
            int cell = Integer.parseInt(move);
            board.place(cell, HUMAN_SYMBOL);
            out.append("  ").append(HUMAN_NAME).append(" turn at cell ").append(cell).append(".\n");
            show(out, board);
            if (!end(out, board, HUMAN_NAME)) {
                // computer's turn
                int comp = computer.chooseCell(board);
                board.place(comp, computer.getSymbol());
                out.append("  ").append(computer.getName()).append(" turn at cell ").append(comp).append(".\n");
                show(out, board);
                if (!end(out, board, computer.getName()))
                    out.append(HUMAN_NAME).append(" enter cell: ");
            }
        }
        // reply: line 1 = new board state for the client to resend next time, rest = text to print
        ch.write(UTF8.encode(board.networkString() + "\n" + out));
    }

    // this is me again, get tired of writing long if else so I create a helper
    private static boolean isNumber(String s) {
        try { Integer.parseInt(s); return true; } catch (NumberFormatException e) { return false; }
    }

    private static boolean end(StringBuilder out, Board board, String who) {
        if (board.isWon()) { out.append(who).append(" wins!\n"); return true; }
        if (board.isFull()) { out.append("It's a draw!\n"); return true; }
        return false;
    }

    private static void show(StringBuilder out, Board board) {
        StringWriter sw = new StringWriter();
        board.display(new PrintWriter(sw, true));
        out.append(sw);
    }
}
