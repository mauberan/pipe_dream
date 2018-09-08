package com.hit.project.pipedream.logic;

import java.util.Observable;
import java.util.Observer;

public class GameBoard implements Observer {
    private int _rowColumnSize;
    private Pipe[][] _board;
    private int _score;

    /* CONSTANTS */
    private static final int TIME_TO_FULL_CAPACITY_SECONDS = 1;
    private static final int SCORE_PER_PIPE = 50;
    /* END CONSTANTS */

    public GameBoard(int rowColumnSize)
    {
        _board = new Pipe[rowColumnSize][rowColumnSize];
        _rowColumnSize = rowColumnSize;
        _score = 0;
    }

    public void startGame(Point firstPipePosition)
    {
        Pipe firstPipe = _board[firstPipePosition.x][firstPipePosition.y];
        if (firstPipe == null)
        {
            //invalid starting point!
            return;
        }
        firstPipe.startFlow();
    }

    public int getScore()
    {
        return _score;
    }

    public boolean addPipeToBoard(Point position, Pipe.PipeType pipeType)
    {
        //check if pipe already exists in this position
        Pipe oldPipe = _board[position.x][position.y];
        if ((oldPipe != null) && (!oldPipe.isRemovable()))
        {
            //pipe exists and cannot be removed
            return false;
        }
        //create new pipe
        Pipe newPipe = new Pipe(TIME_TO_FULL_CAPACITY_SECONDS,position,pipeType);
        //add new pipe to board
        _board[position.x][position.y] = newPipe;
        //add game board as observer to get updated when the pipe is full
        newPipe.addObserver(this);
        //remove pipe from board and update neighbors
        if (position.x > 0)
        {
            //update left neighbor
            updateNeighbor(new Point(position.x - 1,position.y),newPipe,oldPipe,Pipe.DIRECTION_RIGHT);
        }
        if (position.x < (_rowColumnSize-1))
        {
            //update right neighbor
            updateNeighbor(new Point(position.x + 1,position.y),newPipe,oldPipe,Pipe.DIRECTION_LEFT);
        }
        if (position.y > 0)
        {
            //update bottom neighbor
            updateNeighbor(new Point(position.x,position.y-1),newPipe,oldPipe,Pipe.DIRECTION_UP);
        }
        if (position.y < (_rowColumnSize-1))
        {
            //update top neighbor
            updateNeighbor(new Point(position.x,position.y+1),newPipe,oldPipe,Pipe.DIRECTION_DOWN);
        }
        return true;
    }

    private void updateNeighbor(Point position,Pipe newPipe,Pipe oldPipe,int relativeDirection)
    {
        //get pipe by position
        Pipe neighborToUpdate = _board[position.x][position.y];
        //remove the old pipe from this pipe neighbors list
        if (neighborToUpdate != null)
        {
            //remove old pipe
            neighborToUpdate.removeNeighborPipe(oldPipe);
            //add new pipe
            neighborToUpdate.addNeighborPipe(newPipe,relativeDirection);
        }
    }

    public void resetGame()
    {
        //clear score
        _score = 0;
        //clear game board from old pipes
        for (Pipe[] row: _board)
        {
            java.util.Arrays.fill(row, null);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        //update score
        _score += SCORE_PER_PIPE;
    }
}
