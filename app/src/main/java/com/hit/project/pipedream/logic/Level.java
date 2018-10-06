package com.hit.project.pipedream.logic;

import java.io.Serializable;

public class Level implements Serializable {


    public static Level[] Levels = {
            new Level(4,12,50,LevelDifficulty.EASY,60),
            new Level(4,8,50,LevelDifficulty.EASY,60),
            new Level(8,8,50,LevelDifficulty.EASY,60),
            new Level(8,6,50,LevelDifficulty.EASY,40),
            new Level(8,6,50,LevelDifficulty.EASY,40),
            new Level(8,4,100,LevelDifficulty.MEDIUM,40),
            new Level(8,4,100,LevelDifficulty.MEDIUM,40),
            new Level(8,2,100,LevelDifficulty.MEDIUM,30),
            new Level(8,2,100,LevelDifficulty.MEDIUM,30),
            new Level(10,2,100,LevelDifficulty.MEDIUM,30),


            new Level(20,10,120,LevelDifficulty.HARD,30),
            new Level(20,10,120,LevelDifficulty.HARD,30),
            new Level(20,10,120,LevelDifficulty.HARD,30),
            new Level(20,10,120,LevelDifficulty.HARD,30),
            new Level(20,10,120,LevelDifficulty.HARD,30)};

    public enum LevelDifficulty {
        EASY, MEDIUM, HARD
    }

    private int _requiredPipeLength;
    private int _timeBeforeFlow;
    private int _pointsPerPipe;
    private LevelDifficulty _difficulty;
    private int _flowTimeInPipe;

    public Level(int requiredPipeLength, int timeBeforeFlow,  int pointsPerPipe,
            LevelDifficulty difficulty,int flowTimeInPipe)
    {
        _requiredPipeLength = requiredPipeLength;
        _timeBeforeFlow = timeBeforeFlow;
        _pointsPerPipe = pointsPerPipe;
        _difficulty = difficulty;
        _flowTimeInPipe = flowTimeInPipe;
    }

    public int getRequiredPipeLength()
    {
        return _requiredPipeLength;
    }

    public int getFlowTimeInPipe()
    {
        return _flowTimeInPipe;
    }

    public int getTimeBeforeFlow()
    {
        return _timeBeforeFlow;
    }

    public int getPointsPerPipe()
    {
        return _pointsPerPipe;
    }

    public LevelDifficulty getDifficulty()
    {
        return _difficulty;
    }
}
