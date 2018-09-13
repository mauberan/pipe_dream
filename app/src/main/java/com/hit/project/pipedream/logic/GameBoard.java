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

    public void startGame(Point firstPipePosition,Pipe.Directions flowDirection)
    {
        Pipe firstPipe = _board[firstPipePosition.x][firstPipePosition.y];
        if (firstPipe == null)
        {
            //invalid starting point!
            return;
        }
        firstPipe.startFlow(flowDirection);
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
            updateNeighbor(new Point(position.x - 1,position.y),newPipe,oldPipe,Pipe.Directions.RIGHT);
        }
        if (position.x < (_rowColumnSize-1))
        {
            //update right neighbor
            updateNeighbor(new Point(position.x + 1,position.y),newPipe,oldPipe,Pipe.Directions.LEFT);
        }
        if (position.y > 0)
        {
            //update bottom neighbor
            updateNeighbor(new Point(position.x,position.y-1),newPipe,oldPipe,Pipe.Directions.UP);
        }
        if (position.y < (_rowColumnSize-1))
        {
            //update top neighbor
            updateNeighbor(new Point(position.x,position.y+1),newPipe,oldPipe,Pipe.Directions.DOWN);
        }
        return true;
    }

    private void updateNeighbor(Point position,Pipe newPipe,Pipe oldPipe,Pipe.Directions relativeDirection)
    {
        //get pipe by position
        Pipe neighborToUpdate = _board[position.x][position.y];
        //remove the old pipe from this pipe neighbors list
        if (neighborToUpdate == null)
        {
            return;
        }
        //remove old neighbor pipe (only if exists)
        neighborToUpdate.removeNeighborPipe(relativeDirection);
        //add new pipe
        neighborToUpdate.addNeighborPipe(newPipe,relativeDirection);
        //update this pipe with this new neighbor
        newPipe.addNeighborPipe(neighborToUpdate,relativeDirection.getOppositeDirection());
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
        if (!(observable instanceof Pipe) || !(o instanceof Pipe.FlowStatus))
        {
            return;
        }
        Pipe pipe = (Pipe)observable;
        Pipe.FlowStatus status = (Pipe.FlowStatus)o;
        switch (status)
        {
            case FLOW_STARTED_IN_PIPE:
                //print debug information
                System.out.println(String.format("Flow has started. Position:%s pipe type:%s score:%s flow direction:%s",pipe.getPosition(),pipe.getPipeType(),_score,pipe.getFlowDirection()));
                break;
            case FOUND_NEXT_PIPE:
                System.out.println("Pipe is full, found the next linked pipe!");
                //update score
                _score += SCORE_PER_PIPE;
                break;
            case END_OF_PIPE:
                System.out.println("Pipe is full, reached to the end of the pipe!");
                break;
                default:
        }
    }
}
