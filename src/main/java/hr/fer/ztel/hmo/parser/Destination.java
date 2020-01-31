package hr.fer.ztel.hmo.parser;

import sun.security.krb5.internal.crypto.Des;

import java.util.Objects;

public abstract class Destination {
    private Location location;
    private Window window;

    public Destination(Location location, Window window) {
        this.location = location;
        this.window = window;
    }

    public Location getLocation() {
        return location;
    }

    public Window getWindow() {
        return window;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return "Destination{" +
                "location=" + location +
                ", window=" + window +
                '}';
    }
}
