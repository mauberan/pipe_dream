package com.hit.project.pipedream.logic;

import java.io.Serializable;

public class Level implements Serializable {


    public static Level[] Levels = {
            new Level(7,7,50,LevelDifficulty.EASY,40),
            new Level(7,7,50,LevelDifficulty.EASY,40),
            new Level(10,5,50,LevelDifficulty.EASY,40),
            new Level(10,5,50,LevelDifficulty.EASY,35),
            new Level(10,5,50,LevelDifficulty.EASY,35),
            new Level(10,4,100,LevelDifficulty.MEDIUM,30),
            new Level(13,4,100,LevelDifficulty.MEDIUM,30),
            new Level(13,3,100,LevelDifficulty.MEDIUM,30),
            new Level(14,3,100,LevelDifficulty.MEDIUM,30),
            new Level(14,3,100,LevelDifficulty.MEDIUM,30),


            new Level(16,4,120,LevelDifficulty.HARD,30),
            new Level(20,4,120,LevelDifficulty.HARD,30),
            new Level(22,4,120,LevelDifficulty.HARD,30),
            new Level(30,4,120,LevelDifficulty.HARD,30),
            new Level(35,4,120,LevelDifficulty.HARD,30)};

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
