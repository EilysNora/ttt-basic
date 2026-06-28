package com.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;

public class Playertest {
    private Squareboard board;

    @BeforeEach
    void setUp() {
        board = new Squareboard(3);
    }

    // test computer will return symbol 2 when set it
    @Test
    void testComputerplayergetSymbol1() {
        Computerplayer computer = new Computerplayer("Computer", 2);
        assertEquals(2, computer.getSymbol());
    }

    // computer wil choose cell 1 when board is empty
    @Test
    void testComputerchooseCellwhenemptyBoardreturnsCell1() {
        Computerplayer computer = new Computerplayer("Computer", 2);
        int chosen = computer.chooseCell(board);
        assertEquals(1, chosen);
    }

    // computer will skip the invalid cell and choose next cell
    @Test
    void testComputerchooseCellreturnsNextFreeCell() {
        Computerplayer computer = new Computerplayer("Computer", 2);
        board.place(1, 1);
        int chosen = computer.chooseCell(board);
        assertEquals(2, chosen);
    }


    // computer will only choose the empty cell
    @Test
    void testComputerchooseCellisEmpty() {
        Computerplayer computer = new Computerplayer("Computer", 2);
        board.place(1, 1);
        board.place(2, 2);
        board.place(3, 1);
        int chosen = computer.chooseCell(board);
        assertTrue(board.isValidMove(chosen));
    }

    // test human will return symbol 1 when set it
    @Test
    void testHumanplayerget1() {
        String data = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        java.io.PrintWriter writer = new java.io.PrintWriter(outputStream, true);
        
        Humanplayer human = new Humanplayer("Human", 1, reader, writer);
        assertEquals(1, human.getSymbol());
    }

    @Test
    void player10tionTest() throws IOException{
        ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputByteArray, true));

        String data = "q" + System.lineSeparator();
        byte[] byteArray = data.getBytes(); 
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        System.setIn(inputStream);

        Playertest.main(new String[]{"1"});

        byte[] printout = outputByteArray.toByteArray();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(printout), StandardCharsets.UTF_8))) {
            assertEquals("Hello!", reader.readLine());
            assertEquals("| 0 | 0 | 0 |", reader.readLine());
            assertEquals("| 0 | 0 | 0 |", reader.readLine());
            assertEquals("| 0 | 0 | 0 |", reader.readLine());
            assertEquals("Player#1's turn", reader.readLine());
            assertEquals("End of the game", reader.readLine());
            assertNull(reader.readLine());
        }
    }

    private static void main(String[] strings) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'main'");
    }

    }

