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

    public int getX()
    {
        return this.x;

    }

    public int getY()
    {
        return this.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Pipe or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Point)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Point other = (Point) o;

        // Compare the data members and return accordingly
        return (other.x == this.x) && (other.y == this.y);
    }
}
