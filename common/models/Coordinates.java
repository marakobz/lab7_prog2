package models;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private Float x;
    private float y;
    public Coordinates(Float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Float getX() {
        return x;
    }


    public float getY() {
        return y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return getX().equals(that.getX()) && getY()==(that.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "x:" + x + "y:" + y;
    }
}
