package hr.fer.ztel.hmo.parser;

import java.util.Objects;

public class Window {
    private int ready;
    private int due;

    public Window(int ready, int due) {
        this.ready = ready;
        this.due = due;
    }

    public int getReady() {
        return ready;
    }

    public int getDue() {
        return due;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Window window = (Window) o;
        return ready == window.ready &&
                due == window.due;
    }

    @Override
    public String toString() {
        return "Window{" +
                "ready=" + ready +
                ", due=" + due +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(ready, due);
    }
}
