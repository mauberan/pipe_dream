package com.hit.project.pipedream;

import android.graphics.Point;

import com.hit.project.pipedream.logic.GameBoard;
import com.hit.project.pipedream.logic.Pipe;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameBoardUnitTest {

    static final int BOARD_SIZE = 8;
    static GameBoard _board;

    @BeforeClass
    public static void init() {
        _board = new GameBoard(BOARD_SIZE);
    }

    @Before
    public void resetBoard()
    {
        _board.resetGame();
    }

    @Test
    public void addPipe()
    {
        boolean result = _board.addPipeToBoard(new Point(0,0), Pipe.PipeType.HORIZONTAL);
        assertTrue("failed to add pipe",result);
    }

    @Test
    public void testFlow()
    {
        boolean result = _board.addPipeToBoard(new Point(0,6), Pipe.PipeType.HORIZONTAL);
        assertTrue("failed to add pipe (0,6)",result);
        result = _board.addPipeToBoard(new Point(1,6), Pipe.PipeType.BOTTOM_LEFT);
        assertTrue("failed to add pipe (1,6)",result);
        result = _board.addPipeToBoard(new Point(1,5), Pipe.PipeType.VERTICAL);
        assertTrue("failed to add pipe (1,5)",result);
        result = _board.addPipeToBoard(new Point(1,4), Pipe.PipeType.VERTICAL);
        assertTrue("failed to add pipe (1,4)",result);
        result = _board.addPipeToBoard(new Point(1,3), Pipe.PipeType.TOP_RIGHT);
        assertTrue("failed to add pipe (1,3)",result);
        result = _board.addPipeToBoard(new Point(2,3), Pipe.PipeType.HORIZONTAL);
        assertTrue("failed to add pipe (2,3)",result);
        result = _board.addPipeToBoard(new Point(3,3), Pipe.PipeType.HORIZONTAL);
        assertTrue("failed to add pipe (3,3)",result);
    }

    @Test
    public void addInvalidPipe()
    {

    }
}