package com.hit.project.pipedream.logic;

public class Point {

    int x;
    int y;

    public Point()
    {
        x = 0;
        y = 0;
    }

    public Point(int x,int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)",x,y);
    }
}
