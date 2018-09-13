package com.hit.project.pipedream.logic;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class Pipe extends Observable{

    public enum PipeType {TOP_LEFT("Top Left Pipe"),TOP_RIGHT("Top Right Pipe"),
        BOTTOM_LEFT("Bottom Left Pipe"),BOTTOM_RIGHT("Bottom Right Pipe"),
        CROSS("Cross Pipe"),HORIZONTAL("Horizontal Pipe"),VERTICAL("Vertical Pipe");

        private String _description;
        PipeType(String description)
        {
            _description = description;
        }

        @Override
        public String toString() {
            return _description;
        }
    }

    public enum Directions {
        UP("Up",1),DOWN("Down",2),LEFT("Left",4), RIGHT("Right",8),END("End",0);

        static {
            UP._oppositeDirection = DOWN;
            DOWN._oppositeDirection = UP;
            LEFT._oppositeDirection = RIGHT;
            RIGHT._oppositeDirection = LEFT;
        }

        private String _description;
        private int _val;
        Directions _oppositeDirection;

        Directions(String description,int val)
        {
            _description = description;
            _val = val;
        }

        @Override
        public String toString() {
            return _description;
        }

        public int getVal()
        {
            return _val;
        }

        public Directions getOppositeDirection()
        {
            return _oppositeDirection;
        }
    }

    public enum FlowStatus {FOUND_NEXT_PIPE,END_OF_PIPE,FLOW_STARTED_IN_PIPE}

    private boolean _flowStarted;
    private Map<Directions, Pipe>  _connectedPipes;
    private int _timeToFullCapacityInSeconds;
    private Point _position;
    PipeType _pipeType;
    Directions _flowDirection;

    /* Pipe directions */
    public static final Map<PipeType, Integer> AvailableDirections = new HashMap<PipeType, Integer>() {{
        put(PipeType.TOP_LEFT, Directions.LEFT.getVal() | Directions.UP.getVal());
        put(PipeType.TOP_RIGHT, Directions.UP.getVal() | Directions.RIGHT.getVal());
        put(PipeType.BOTTOM_LEFT, Directions.DOWN.getVal() | Directions.LEFT.getVal());
        put(PipeType.BOTTOM_RIGHT, Directions.DOWN.getVal() | Directions.RIGHT.getVal());
        put(PipeType.CROSS, Directions.LEFT.getVal() | Directions.RIGHT.getVal() | Directions.DOWN.getVal() | Directions.UP.getVal());
        put(PipeType.HORIZONTAL, Directions.LEFT.getVal() | Directions.RIGHT.getVal());
        put(PipeType.VERTICAL, Directions.UP.getVal() | Directions.DOWN.getVal());
    }};
    /* end pipe direction*/

    public Pipe(int timeToFullCapacityInSeconds, Point position, PipeType pipeDirection)
    {
        _flowStarted = false;
        _connectedPipes = new HashMap<>();
        _position = position;
        _timeToFullCapacityInSeconds = timeToFullCapacityInSeconds;
        _pipeType = pipeDirection;
        _flowDirection = Directions.END;
    }

    public void addNeighborPipe(Pipe neighbor,Directions relativeDirection) {
        Integer neighborAvailableDirections = AvailableDirections.get(neighbor._pipeType);
        Integer thisAvailableDirections = AvailableDirections.get(_pipeType);
        switch (relativeDirection)
        {
            case UP:
                if ( ((thisAvailableDirections & Directions.UP.getVal()) == 0) || ((neighborAvailableDirections & Directions.DOWN.getVal()) == 0) )
                {
                    return;
                }
                break;
            case DOWN:
                if ( ((thisAvailableDirections & Directions.DOWN.getVal()) == 0) || ((neighborAvailableDirections & Directions.UP.getVal()) == 0) )
                {
                    return;
                }
                break;
            case  LEFT:
                if ( ((thisAvailableDirections & Directions.LEFT.getVal()) == 0) || ((neighborAvailableDirections & Directions.RIGHT.getVal()) == 0) )
                {
                    return;
                }
                break;
            case RIGHT:
                if ( ((thisAvailableDirections & Directions.RIGHT.getVal()) == 0) || ((neighborAvailableDirections & Directions.LEFT.getVal()) == 0) )
                {
                    return;
                }
                break;
                default:
                    return;
        }
        //this neighbor has a shared joint! connect with it.
        _connectedPipes.put(relativeDirection,neighbor);
    }

    public void removeNeighborPipe(Directions neighborDirection)
    {
        _connectedPipes.remove(neighborDirection);
    }

    public void startFlow(Directions flowDirection)
    {
        //mark flow started to block the user from removing this pipe
        _flowStarted = true;
        //set flow direction to be used by startNeighborsFlow to find suitable pipe
        _flowDirection = flowDirection;
        //notify game board that flow has started in this pipe (start animation)
        setChanged();
        notifyObservers(FlowStatus.FLOW_STARTED_IN_PIPE);
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
        setChanged();
        //go over the list of neighbors and start flow for each one of them
        if (_connectedPipes.isEmpty())
        {
            //no more linked pipes! end current game
            notifyObservers(FlowStatus.END_OF_PIPE);
            return;

        }

        //handle cross pipe differently from other pipes - may have more than one neighbor
        if (_pipeType == PipeType.CROSS)
        {
            Pipe nextPipe = _connectedPipes.get(_flowDirection);
            if (nextPipe == null)
            {
                //no neighbor matching the flow direction
                notifyObservers(FlowStatus.END_OF_PIPE);
            } else {
                //notify game board
                notifyObservers(FlowStatus.FOUND_NEXT_PIPE);
                //start flow in same direction
                nextPipe.startFlow(_flowDirection);
            }
            return;
        } else {
            for (Map.Entry<Directions, Pipe> pipeEntry : _connectedPipes.entrySet()) {
                Pipe neighborPipe = pipeEntry.getValue();
                if ((neighborPipe._flowStarted == true) && (neighborPipe._pipeType != PipeType.CROSS))
                {
                    //this neighbor was already visited and pipe type is nor cross (which allows more than one visit)
                    continue;
                }
                notifyObservers(FlowStatus.FOUND_NEXT_PIPE);
                if (neighborPipe.getPipeType() == PipeType.HORIZONTAL || neighborPipe.getPipeType() == PipeType.VERTICAL || neighborPipe.getPipeType() == PipeType.CROSS)
                {
                    //same flow direction
                    neighborPipe.startFlow(_flowDirection);
                    return;
                } else {
                    int newDirection = pipeEntry.getKey().getOppositeDirection().getVal() ^ AvailableDirections.get(neighborPipe.getPipeType());
                    for (Directions newFlowDirection : Directions.values()) {
                        if (newFlowDirection.getVal() == newDirection) {
                            neighborPipe.startFlow(newFlowDirection);
                            return;
                        }
                    }
                }
            }
            notifyObservers(FlowStatus.END_OF_PIPE);
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
        return (p._position.y == _position.y) && (p._position.x == _position.x);
    }

    public Point getPosition()
    {
        return _position;
    }

    public PipeType getPipeType()
    {
        return _pipeType;
    }

    public Directions getFlowDirection()
    {
        return _flowDirection;
    }
}
