package hr.fer.ztel.hmo.ga;

import hr.fer.ztel.hmo.ga.evauluation.RouteEvaluator;

import java.util.List;
import java.util.Objects;

public class Chromosome {
    private int vehicleNumber;
    private List<Route> routes;

    public Chromosome(int vehicleNumber, List<Route> routes) {
        this.vehicleNumber = vehicleNumber;
        this.routes = routes;
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chromosome that = (Chromosome) o;
        return vehicleNumber == that.vehicleNumber &&
                Objects.equals(routes, that.routes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleNumber, routes);
    }
}
