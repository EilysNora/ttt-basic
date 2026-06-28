package com.tictactoe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


public class QuitTest {

    private final java.io.PrintStream originalOut = System.out;
    private final java.io.InputStream  originalIn  = System.in;

    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectStreams() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    @Test
    void testQuitWithLowercaseQ_printsEndOfGame() {
        feedInput("q\n");

        Squareboard board  = new Squareboard(3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        java.io.PrintWriter writer = new java.io.PrintWriter(System.out, true);
        Humanplayer human  = new Humanplayer("Player#1", 1, reader, writer);
        try {
            human.chooseCell(board);
            fail("chooseCell() should have terminated the game when input is \"q\"");
        } catch (QuitException e) {
        } catch (SystemExitException e) {
        }

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(
            output.contains("End of the game"),
            "Expected \"End of the game\" in output but got:\n" + output
        );
    }

    @Test
    void testQuitWithLowercaseQ_gameEndsImmediately() {
        feedInput("q\n");

        Board       board   = new Squareboard(3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        java.io.PrintWriter writer = new java.io.PrintWriter(System.out, true);
        Player      human   = new Humanplayer("Player#1", 1, reader, writer);
        Player      computer = new Computerplayer("Player#2", 2);

        try {
            new Game(board, human, computer, writer).run();
            fail("Game.run() should have stopped when human typed \"q\"");
        } catch (QuitException | SystemExitException ignored) {
        }

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(
            output.contains("End of the game"),
            "Expected \"End of the game\" in output.\nActual output:\n" + output
        );
        int idx = output.indexOf("End of the game");
        String afterQuit = output.substring(idx + "End of the game".length()).trim();
        assertTrue(
            afterQuit.isEmpty(),
            "No output should follow \"End of the game\" but found:\n" + afterQuit
        );
    }

    @Test
    void testQuitUppercaseQ_isNotQuit_showsInvalidMessage() {
        feedInput("Q\n1\n");

        Squareboard board = new Squareboard(3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        java.io.PrintWriter writer = new java.io.PrintWriter(System.out, true);
        Humanplayer human = new Humanplayer("Player#1", 1, reader, writer);

        int cell = human.chooseCell(board);

        String output = capturedOut.toString(StandardCharsets.UTF_8);

        assertEquals(1, cell, "Recovery move should be cell 1");
        assertTrue(
            output.contains("Please, input a valid number [1-9]"),
            "Expected invalid-number message for \"Q\".\nActual:\n" + output
        );
        assertFalse(
            output.contains("End of the game"),
            "\"Q\" must not trigger end-of-game."
        );
    }

    @Test
    void testQuitLeadingSpaceQ_isNotQuit_showsInvalidMessage() {
        feedInput(" q\n1\n");

        Squareboard board = new Squareboard(3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        java.io.PrintWriter writer = new java.io.PrintWriter(System.out, true);
        Humanplayer human = new Humanplayer("Player#1", 1, reader, writer);

        int cell = human.chooseCell(board);

        String output = capturedOut.toString(StandardCharsets.UTF_8);

        assertEquals(1, cell);
        assertTrue(
            output.contains("Please, input a valid number [1-9]"),
            "Expected invalid-number message for \" q\".\nActual:\n" + output
        );
        assertFalse(output.contains("End of the game"));
    }

    @Test
    void testQuitTrailingSpaceQ_isNotQuit_showsInvalidMessage() {
        feedInput("q \n1\n");

        Squareboard board = new Squareboard(3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        java.io.PrintWriter writer = new java.io.PrintWriter(System.out, true);
        Humanplayer human = new Humanplayer("Player#1", 1, reader, writer);

        int cell = human.chooseCell(board);

        String output = capturedOut.toString(StandardCharsets.UTF_8);

        assertEquals(1, cell);
        assertTrue(
            output.contains("Please, input a valid number [1-9]"),
            "Expected invalid-number message for \"q \".\nActual:\n" + output
        );
        assertFalse(output.contains("End of the game"));
    }

    private void feedInput(String text) {
        System.setIn(new ByteArrayInputStream(
            text.getBytes(StandardCharsets.UTF_8)));
    }

    public static class QuitException extends RuntimeException {
        public QuitException() { super("User requested quit"); }
    }

    public static class SystemExitException extends RuntimeException {
        public SystemExitException(int status) {
            super("System.exit(" + status + ") called");
        }
    }
}
