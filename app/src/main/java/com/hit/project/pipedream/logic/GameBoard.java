package com.hit.project.pipedream.logic;

import java.util.Observable;
import java.util.Random;
import java.util.Observer;

public class GameBoard extends Observable implements Observer {
    private int _rowColumnSize;
    private Pipe[][] _board;
    private int _filledPipes;
    private Pipe _currentPipe;
    private Pipe _firstPipe;
    private int _level;
    private boolean _isInGame;

    /* CONSTANTS */
    private static final int SCORE_PER_PIPE = 50;
    private static final int TIME_BEFORE_FLOW = 12*1000;
    /* END CONSTANTS */

    public GameBoard(int rowColumnSize)
    {
        _board = new Pipe[rowColumnSize][rowColumnSize];
        _rowColumnSize = rowColumnSize;
        _firstPipe = getRandomFirstPipe();
        _board[_firstPipe.getPosition().x][_firstPipe.getPosition().y] = _firstPipe;
        _firstPipe.addObserver(this);
        _level = 0;
        _filledPipes = 0;
        _isInGame = false;
    }

    public void injectFirstPipe(Pipe firstPipe)
    {
        if (_firstPipe != null)
        {
            _board[_firstPipe.getPosition().x][_firstPipe.getPosition().y] = null;
        }
        _firstPipe = firstPipe;
        _board[_firstPipe.getPosition().x][_firstPipe.getPosition().y] = _firstPipe;
        _firstPipe.addObserver(this);
    }

    private Pipe getRandomFirstPipe()
    {
        //get random start position
        Random rand = new Random();
        //50 is the maximum and the 1 is our minimum
        int  row = rand.nextInt(_rowColumnSize - 3) + 1;
        int column = rand.nextInt(_rowColumnSize-3) + 1;
        double direction = Math.pow(2, rand.nextInt(4));
        Pipe firstPipe;
        if (direction == Pipe.Directions.RIGHT.getVal())
        {
            firstPipe = new Pipe(new Point(row,column), Pipe.PipeType.START_RIGHT);
            firstPipe.setFlowDirection(Pipe.Directions.RIGHT);
        } else if (direction == Pipe.Directions.LEFT.getVal())
        {
            firstPipe = new Pipe(new Point(row,column), Pipe.PipeType.START_LEFT);
            firstPipe.setFlowDirection(Pipe.Directions.LEFT);
        } else if (direction == Pipe.Directions.UP.getVal())
        {
            firstPipe = new Pipe(new Point(row,column), Pipe.PipeType.START_UP);
            firstPipe.setFlowDirection(Pipe.Directions.UP);
        } else if (direction == Pipe.Directions.DOWN.getVal()) {
            firstPipe = new Pipe(new Point(row,column), Pipe.PipeType.START_DOWN);
            firstPipe.setFlowDirection(Pipe.Directions.DOWN);
        } else {
            firstPipe = null;
        }
        return firstPipe;
    }

    public Pipe getFirstPipe()
    {
        return _firstPipe;
    }

    public void startGame()
    {
        if (_firstPipe == null)
        {
            //invalid starting point!
            return;
        }
        _currentPipe = _firstPipe;
        _isInGame = true;
        _firstPipe.startFlow();
    }

    public void notifyPipeIsFull()
    {
        if (_currentPipe == null)
        {
            return;
        }
        _currentPipe.pipeIsFull();
    }

    public int getScore()
    {
        return _filledPipes * Level.Levels[_level].getPointsPerPipe();
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
        Pipe newPipe = new Pipe(position,pipeType);
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
        if (shouldLoadNextLevel())
        {
            _level += 1;
            if (_level >= Level.Levels.length)
            {
                _level = 0;
                System.out.println("User has finished the game! Set current level to level:0");
            }
        }
        //clear score
        _filledPipes = 0;
        //clear game board from old pipes
        for (Pipe[] row: _board)
        {
            java.util.Arrays.fill(row, null);
        }
        //set new first pipe
        _firstPipe = getRandomFirstPipe();
        _board[_firstPipe.getPosition().getX()][_firstPipe.getPosition().getY()] = _firstPipe;
        _firstPipe.addObserver(this);
        _currentPipe = null;
        _isInGame = false;
    }

    public Level getCurrentLevel()
    {
        return Level.Levels[_level];
    }

    @Override
    public void update(Observable observable, Object o) {
        if (!(observable instanceof Pipe) || !(o instanceof Pipe.FlowStatus))
        {
            return;
        }
        Pipe pipe = (Pipe)observable;
        Pipe.FlowStatus status = (Pipe.FlowStatus)o;
        setChanged();
        _currentPipe = pipe;

        switch (status)
        {
            case FLOW_STARTED_IN_PIPE:
                //print debug information
                System.out.println(String.format("Flow has started. Position:%s pipe type:%s score:%s flow direction:%s",pipe.getPosition(),pipe.getPipeType(),
                        getScore(),pipe.getFlowDirection()));
                _filledPipes += 1;
                notifyObservers(Pipe.FlowStatus.FLOW_STARTED_IN_PIPE);
                break;
            case FOUND_NEXT_PIPE:
                System.out.println("Pipe is full, found the next linked pipe!");
                //update score
                notifyObservers(Pipe.FlowStatus.FOUND_NEXT_PIPE);
                break;
            case END_OF_PIPE:
                //update isInGame flag as there are no more pipes
                _isInGame = false;
                System.out.println("Pipe is full, reached to the end of the pipe!");
                if (shouldLoadNextLevel())
                {
                    System.out.println("Player has finished the level successfully");
                    if (_level == (Level.Levels.length-1))
                    {
                        notifyObservers(Pipe.FlowStatus.NO_MORE_LEVELS);
                    } else {
                        notifyObservers(Pipe.FlowStatus.NEXT_LEVEL);
                    }

                } else {
                    System.out.println("Player did nit finished the level successfully");
                    notifyObservers(Pipe.FlowStatus.GAMEOVER);
                }
                break;
                default:
        }
    }

    public boolean getIsInGame()
    {
        return _isInGame;
    }

    private boolean shouldLoadNextLevel()
    {
        return _filledPipes >= Level.Levels[_level].getRequiredPipeLength();
    }

    public Pipe getCurrentPipe()
    {
        return _currentPipe;
    }

}
