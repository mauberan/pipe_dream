package com.hit.project.pipedream.data;

import java.io.Serializable;
import java.util.Date;

public class ScoreRecord implements Serializable {

    int _score;
    String _nickname;
    Date _date;

    public ScoreRecord(String nickname,int score, Date date)
    {
        _score = score;
        _nickname = nickname;
        _date = date;
    }

    public int getScore()
    {
        return _score;
    }

    public String getNickname()
    {
        return _nickname;
    }

    public Date getDate()
    {
        return _date;
    }

    @Override
    public String toString() {
        return String.format("Nickname:%s Score:%d Date:%s",_nickname,_score,_date.toString());
    }
}
