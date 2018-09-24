package com.hit.project.pipedream.logic;

public class Level {


    public static Level[] Levels = {new Level(5,10,50,LevelDifficulty.EASY,1000),
            new Level(4,10,50,LevelDifficulty.EASY,1000),
            new Level(5,10,50,LevelDifficulty.EASY,1000),
            new Level(6,10,50,LevelDifficulty.EASY,1000),
            new Level(7,10,50,LevelDifficulty.EASY,1000),
            new Level(8,10,100,LevelDifficulty.MEDIUM,700),
            new Level(8,10,100,LevelDifficulty.MEDIUM,700),
            new Level(9,10,100,LevelDifficulty.MEDIUM,700),
            new Level(9,10,100,LevelDifficulty.MEDIUM,700),
            new Level(10,10,100,LevelDifficulty.MEDIUM,700),
            new Level(11,10,120,LevelDifficulty.HARD,400),
            new Level(11,10,120,LevelDifficulty.HARD,400),
            new Level(12,10,120,LevelDifficulty.HARD,400),
            new Level(12,10,120,LevelDifficulty.HARD,400),
            new Level(13,10,120,LevelDifficulty.HARD,400)};

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
