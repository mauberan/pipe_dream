package com.hit.project.pipedream.logic;

public class Level {

    public enum LevelDifficulty {
        EASY, MEDIUM, HARD
    }

    private int _requiredPipeLength;
    private int _timeBeforeFlow;
    private int _flowTimeInPipe;
    private LevelDifficulty _difficulty;

    public Level(int requiredPipeLength, int timeBeforeFlow,  int flowTimeInPipe,
            LevelDifficulty difficulty)
    {
        _requiredPipeLength = requiredPipeLength;
        _timeBeforeFlow = timeBeforeFlow;
        _flowTimeInPipe = flowTimeInPipe;
        _difficulty = difficulty;
    }

    public int getRequiredPipeLength()
    {
        return _requiredPipeLength;
    }

    public int getTimeBeforeFlow()
    {
        return _timeBeforeFlow;
    }

    public int getflowTimeInPipe()
    {
        return _flowTimeInPipe;
    }

    public LevelDifficulty getDifficulty()
    {
        return _difficulty;
    }
}
