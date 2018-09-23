package com.hit.project.pipedream.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class Pipe extends Observable{

    public enum PipeType {TOP_LEFT("Top Left Pipe"),TOP_RIGHT("Top Right Pipe"),
        BOTTOM_LEFT("Bottom Left Pipe"),BOTTOM_RIGHT("Bottom Right Pipe"),
        CROSS("Cross Pipe"),HORIZONTAL("Horizontal Pipe"),VERTICAL("Vertical Pipe"),START_RIGHT("Start Right"),
        START_LEFT("Start Left"),START_UP("Start Up"),START_DOWN("Start Down");

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

    public enum FlowStatus {FOUND_NEXT_PIPE,GAMEOVER,FLOW_STARTED_IN_PIPE,NEXT_LEVEL,END_OF_PIPE,NO_MORE_LEVELS}

    private int _numOfVisits;
    private Map<Directions, Pipe>  _connectedPipes;
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
        put(PipeType.START_RIGHT, Directions.RIGHT.getVal());
        put(PipeType.START_LEFT, Directions.LEFT.getVal());
        put(PipeType.START_DOWN, Directions.DOWN.getVal());
        put(PipeType.START_UP, Directions.UP.getVal());
    }};
    /* end pipe direction*/

    public Pipe(Point position, PipeType pipeDirection)
    {
        _numOfVisits = 0;
        _connectedPipes = new HashMap<>();
        _position = position;
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

    public void startFlow()
    {
        //mark flow started to block the user from removing this pipe
        _numOfVisits += 1;
        //notify game board that flow has started in this pipe (start animation)
        setChanged();
        notifyObservers(FlowStatus.FLOW_STARTED_IN_PIPE);
    }

    public void pipeIsFull()
    {
        startNeighborsFlow();
    }

    private void startNeighborsFlow()
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
            if ( nextPipe == null || ( (nextPipe._pipeType != PipeType.CROSS) && (nextPipe._numOfVisits > 0) ) )
            {
                //no neighbor matching the flow direction
                notifyObservers(FlowStatus.END_OF_PIPE);
                return;
            } else if (nextPipe._pipeType == PipeType.CROSS)
            {
                nextPipe.setFlowDirection(_flowDirection);
            }
            else {
                //find direction by exit from pipe
                int newDirection = _flowDirection.getOppositeDirection().getVal() ^ AvailableDirections.get(nextPipe.getPipeType());
                for (Directions directionOption : Directions.values()) {
                    if (directionOption.getVal() == newDirection) {
                        //start flow in same direction
                        nextPipe.setFlowDirection(directionOption);
                        break;
                    }
                }
            }
            //notify game board
            notifyObservers(FlowStatus.FOUND_NEXT_PIPE);
            //remove link so we don't visit the same path again
            nextPipe.removeNeighborPipe(_flowDirection._oppositeDirection);
            removeNeighborPipe(_flowDirection);
            nextPipe.startFlow();
            return;
        } else {
            for (Map.Entry<Directions, Pipe> pipeEntry : _connectedPipes.entrySet()) {
                Pipe neighborPipe = pipeEntry.getValue();
                if ((neighborPipe._numOfVisits > 0) && (neighborPipe._pipeType != PipeType.CROSS))
                {
                    //this neighbor was already visited and pipe type is nor cross (which allows more than one visit)
                    continue;
                }
                if (neighborPipe.getPipeType() == PipeType.HORIZONTAL || neighborPipe.getPipeType() == PipeType.VERTICAL || neighborPipe.getPipeType() == PipeType.CROSS)
                {
                    //same flow direction
                    notifyObservers(FlowStatus.FOUND_NEXT_PIPE);
                    neighborPipe.setFlowDirection(_flowDirection);
                    neighborPipe.startFlow();
                    return;
                } else {
                    int newDirection = pipeEntry.getKey().getOppositeDirection().getVal() ^ AvailableDirections.get(neighborPipe.getPipeType());
                    for (Directions directionOption : Directions.values()) {
                        if (directionOption.getVal() == newDirection) {
                            notifyObservers(FlowStatus.FOUND_NEXT_PIPE);
                            neighborPipe.setFlowDirection(directionOption);
                            neighborPipe.startFlow();
                            return;
                        }
                    }
                }
            }
        }
        notifyObservers(FlowStatus.END_OF_PIPE);
    }

    public boolean isRemovable()
    {
        return _numOfVisits == 0;
    }

    public int getNumOfVisits()
    {
        return _numOfVisits;
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
        return p._position.equals(_position);
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

    public void setFlowDirection(Directions flowDirection)
    {
        _flowDirection = flowDirection;
    }
}
