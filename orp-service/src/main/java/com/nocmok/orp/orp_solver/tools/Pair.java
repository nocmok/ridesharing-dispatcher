package com.nocmok.orp.orp_solver.tools;

import java.util.Objects;

public class Pair<T> {

    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?> pair = (Pair<?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override public int hashCode() {
        return Objects.hash(first, second);
    }
}
