package com.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Squareboardtest {
    private Squareboard board;

    // create the first sample for test
    @BeforeEach
    void setUp() {
        board = new Squareboard(3);
    }

    // size of 3x3 should be 9
    @Test
    void testSize3x3Boardis9() {
        assertEquals(9, board.getSize());
    }

    // size of 4x4 should be 16
    @Test
    void testSize4x4Boardis16() {
        Squareboard big = new Squareboard(4);
        assertEquals(16, big.getSize());
    }

    //cell in every position in beginning should be empty
    @Test    
    void testCellBoardAreEmpty() {
        for (int i = 1; i <= board.getSize(); i++) {
            assertEquals(Squareboard.EMPTY, board.getCell(i));
        }
    }

    //test value of cell when it place move
    @Test
    void testCellafterPlacereturnsCorrectSymbol() {
        board.place(5, 1);
        assertEquals(1, board.getCell(5));
    }

    // cell invalid when player made it
    @Test
    void testoccupiedCellreturnsFalse() {
        board.place(3, 1);
        assertFalse(board.isValidMove(3));
    }

    // cell is not in the range of 3x3 size (example: cell 10)
    @Test
    void testValidcelloversizereturnsFalse() {
        assertFalse(board.isValidMove(10));
    }

    // negative cell is not valid
    @Test
    void testValidnegativeCellreturnsFalse() {
        assertFalse(board.isValidMove(-1));
    }

    // test correct cell of the player1 mark
    @Test
    void testPlaceplayer1storedCorrectly() {
        board.place(1, 1);
        assertEquals(1, board.getCell(1));
    }

    // test correct cell of the player2 mark
    @Test
    void testPlaceplayer2storedCorrectly() {
        board.place(9, 2);
        assertEquals(2, board.getCell(9));
    }

    // test the beginning board is not full
    @Test
    void testIsFull_freshBoard_returnsFalse() {
        assertFalse(board.isFull());
    }

    // test the board is not full even some cell is filled
    @Test
    void testIsFullwhenmanyFilledreturnsFalse() {
        board.place(1, 1);
        board.place(2, 2);
        assertFalse(board.isFull());
    }

    // test board full when 9 cell is filled
    @Test
    void testIsFullwhenallCellsFilledreturnsTrue() {
        for (int i = 1; i <= board.getSize(); i++) {
            board.place(i, (i % 2 == 0) ? 2 : 1);
        }
        assertTrue(board.isFull());
    }

    // test show win when player 1 fill cell 1,2,3
    @Test
    void testIsWontRowFilledreturnsTrue() {
        board.place(1, 1); board.place(2, 1); board.place(3, 1);
        assertTrue(board.isWon());
    }


    // test show win when player 1 fill cell 1,4,7
    @Test
    void testIsWonColumnFilledreturnsTrue() {
        board.place(1, 1); board.place(4, 1); board.place(7, 1);
        assertTrue(board.isWon());
    }

    // test show win when player 1 fill cell 1,5,9
    @Test
    void testIsWonmainDiagonalFilledreturnsTrue() {
        board.place(1, 2); board.place(5, 2); board.place(9, 2);
        assertTrue(board.isWon());
    }

}