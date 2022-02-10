package com.nocmok.orp.proto.pojo;

import java.util.Objects;

public class GPS {

    public final double x;
    public final double y;

    public GPS(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override public String toString() {
        return "GPS{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GPS gps = (GPS) o;
        return Double.compare(gps.x, x) == 0 && Double.compare(gps.y, y) == 0;
    }

    @Override public int hashCode() {
        return Objects.hash(x, y);
    }
}
