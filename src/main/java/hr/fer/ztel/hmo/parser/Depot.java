package hr.fer.ztel.hmo.parser;

public class Depot extends Destination{
    public Depot(Location location, Window window) {
        super(location, window);
    }

    @Override
    public String toString() {
        return "Depot{} " + super.toString();
    }
}
