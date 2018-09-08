package com.hit.project.pipedream.logic;

import android.graphics.Point;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class Pipe extends Observable{

    public enum PipeType {TOP_LEFT,TOP_RIGHT,
        BOTTOM_LEFT,BOTTOM_RIGHT,CROSS,HORIZONTAL,VERTICAL}

    private boolean _flowStarted;
    private List<Pipe>  _connectedPipes;
    private int _timeToFullCapacityInSeconds;
    private Point _position;
    PipeType _pipeType;

    /* start directions */
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 4;
    public static final int DIRECTION_RIGHT = 8;
    /* end directions */

    /* Pipe directions */
    Map<PipeType, Integer> AvailableDirections = new HashMap<PipeType, Integer>() {{
        put(PipeType.TOP_LEFT, DIRECTION_LEFT | DIRECTION_UP);
        put(PipeType.TOP_RIGHT, DIRECTION_UP | DIRECTION_RIGHT);
        put(PipeType.BOTTOM_LEFT, DIRECTION_DOWN | DIRECTION_LEFT);
        put(PipeType.BOTTOM_RIGHT, DIRECTION_DOWN | DIRECTION_RIGHT);
        put(PipeType.CROSS, DIRECTION_LEFT | DIRECTION_RIGHT | DIRECTION_DOWN | DIRECTION_UP);
        put(PipeType.HORIZONTAL, DIRECTION_LEFT | DIRECTION_RIGHT);
        put(PipeType.VERTICAL, DIRECTION_UP | DIRECTION_DOWN);
    }};
    /* end pipe direction*/

    public Pipe(int timeToFullCapacityInSeconds, Point position, PipeType pipeDirection)
    {
        _flowStarted = false;
        _connectedPipes = new ArrayList<>();
        _position = position;
        _timeToFullCapacityInSeconds = timeToFullCapacityInSeconds;
        _pipeType = pipeDirection;
    }

    public void addNeighborPipe(Pipe neighbor,int relativeDirection) {
        Integer neighborAvailableDirections = AvailableDirections.get(neighbor._pipeType);
        Integer thisAvailableDirections = AvailableDirections.get(_pipeType);
        switch (relativeDirection)
        {
            case DIRECTION_UP:
                if ( ((thisAvailableDirections & DIRECTION_UP) == 0) || ((neighborAvailableDirections & DIRECTION_DOWN) == 0) )
                {
                    return;
                }
                break;
            case DIRECTION_DOWN:
                if ( ((thisAvailableDirections & DIRECTION_DOWN) == 0) || ((neighborAvailableDirections & DIRECTION_UP) == 0) )
                {
                    return;
                }
                break;
            case  DIRECTION_LEFT:
                if ( ((thisAvailableDirections & DIRECTION_LEFT) == 0) || ((neighborAvailableDirections & DIRECTION_RIGHT) == 0) )
                {
                    return;
                }
                break;
            case DIRECTION_RIGHT:
                if ( ((thisAvailableDirections & DIRECTION_RIGHT) == 0) || ((neighborAvailableDirections & DIRECTION_LEFT) == 0) )
                {
                    return;
                }
                break;
                default:
                    return;
        }
        //this neighbor has a shared joint! connect with it.
        _connectedPipes.add(neighbor);
    }

    public void removeNeighborPipe(Pipe removedNeighbor)
    {
        _connectedPipes.remove(removedNeighbor);
    }

    public void startFlow()
    {
        //mark flow started to block the user from removing this pipe
        _flowStarted = true;
        //create task to execute when the pipe gets full
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startNeighborsFlow();
            }
        };
        //create the timer (on timeout will execute the task)
        Timer timer = new Timer("flowTimer");
        //schedule task to run after _timeToFullCapacity * 1000 (seconds to milliseconds)
        timer.schedule(task,_timeToFullCapacityInSeconds * 1000);
    }

    public void startNeighborsFlow()
    {
        //update game board (to increase score...)
        setChanged();
        notifyObservers();
        //go over the list of neighbors and start flow for each one of them
        for (Pipe neighborPipe : _connectedPipes)
        {
            //TODO:verify that this function is not blocking (we might have more than one neighbor)
            neighborPipe.startFlow();
        }
    }

    public boolean isRemovable()
    {
        return _flowStarted == false;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Pipe or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Pipe)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Pipe p = (Pipe) o;

        // Compare the data members and return accordingly
        return _position.equals(((Pipe) o)._position);
    }
}
