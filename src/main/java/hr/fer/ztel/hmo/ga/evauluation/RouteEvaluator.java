package hr.fer.ztel.hmo.ga.evauluation;

import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.parser.Customer;
import hr.fer.ztel.hmo.parser.Depot;
import hr.fer.ztel.hmo.parser.Destination;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.LinkedList;
import java.util.List;

public class RouteEvaluator {


    public RouteEvaluator() {
    }

    public RouteEvaluationResult evaluate(Route route, Depot depot) {
        return evaluate(route, depot, true);
    }

    public RouteEvaluationResult evaluate(Route route, Depot depot, boolean withDepot) {
        double totalDistance = 0;
        int totalTime = 0;
        int totalCapacityUsed = 0;
        Destination previous = depot;
        for (Customer customer : route.getCustomerList()) {
            totalCapacityUsed += customer.getDemand();
            double distance = previous.getLocation().distanceTo(customer.getLocation());
            totalDistance += distance;
            totalTime += Math.ceil(distance);
            if (totalTime < customer.getWindow().getReady()) {
                totalTime = customer.getWindow().getReady();
            }
            totalTime += customer.getServiceTime();
            previous = customer;
        }
        if (withDepot) {
            double distance = previous.getLocation().distanceTo(depot.getLocation());
            totalDistance += distance;
            totalTime += Math.ceil(distance);
        }
        return new RouteEvaluationResult(
                totalDistance,
                totalTime,
                totalCapacityUsed
        );
    }

    public List<Integer> calculateServiceTimes(Route route, Depot depot) {
        int totalTime = 0;
        Destination previous = depot;
        List<Integer> serviceTimes = new LinkedList<>();
        serviceTimes.add(0);
        for (Customer customer : route.getCustomerList()) {
            double distance = previous.getLocation().distanceTo(customer.getLocation());
            totalTime += Math.ceil(distance);
            if (totalTime < customer.getWindow().getReady()) {
                totalTime = customer.getWindow().getReady();
            }
            serviceTimes.add(totalTime);
            totalTime += customer.getServiceTime();
            previous = customer;
        }
        double distance = previous.getLocation().distanceTo(depot.getLocation());
        totalTime += Math.ceil(distance);
        serviceTimes.add(totalTime);
        return serviceTimes;
    }


    public static class RouteEvaluationResult {
        private double totalDistance;
        private int totalTime;
        private int totalCapacityUsed;

        public RouteEvaluationResult() {
            this(0, 0, 0);
        }

        public RouteEvaluationResult(double totalDistance, int totalTime, int totalCapacityUsed) {
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
            this.totalCapacityUsed = totalCapacityUsed;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public int getTotalCapacityUsed() {
            return totalCapacityUsed;
        }
    }
}
