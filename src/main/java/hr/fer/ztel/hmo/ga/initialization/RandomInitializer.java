package hr.fer.ztel.hmo.ga.initialization;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.ga.Utils;
import hr.fer.ztel.hmo.ga.evauluation.RouteEvaluator;
import hr.fer.ztel.hmo.parser.Customer;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomInitializer implements ChromosomeInitializer {
    private int lowerVehicleCount;
    private int upperVehicleCount;
    private Instance instance;
    private static final Random random = new Random();
    private RouteEvaluator routeEvaluator;

    public RandomInitializer(int lowerVehicleCount, int upperVehicleCount, Instance instance, RouteEvaluator routeEvaluator) {
        this.lowerVehicleCount = lowerVehicleCount;
        this.upperVehicleCount = upperVehicleCount;
        this.instance = instance;
        this.routeEvaluator = routeEvaluator;
    }

    @Override
    public Chromosome initialize() {
        int vehicleCount = lowerVehicleCount + random.nextInt(upperVehicleCount - lowerVehicleCount + 1);
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < vehicleCount; i++) {
            routes.add(new Route(new LinkedList<>()));
        }
        LinkedList<Customer> customersOrderedByDueTime = new LinkedList<>(instance.getCustomers());
        customersOrderedByDueTime.sort(Comparator.comparingInt((Customer c) -> c.getWindow().getDue()));
        while (!customersOrderedByDueTime.isEmpty()) {
            Customer nextCustomer = customersOrderedByDueTime.getFirst();
            double rand = ThreadLocalRandom.current().nextDouble();
            if (rand < 0.8) {
                Utils.insertCustomerIntoRoutesAtEnd(routes, instance, routeEvaluator, nextCustomer);
            } else if (rand < 0.9) {
                Utils.insertCustomerIntoRoutes(routes, instance, routeEvaluator, nextCustomer, false, true);
            } else {
                Utils.insertCustomerIntoRoutesMinDist(routes, instance, routeEvaluator, nextCustomer);
            }
            customersOrderedByDueTime.removeFirst();
        }
        routes
                .stream()
                .filter(route -> route.getCustomerList().size() == 0)
                .collect(Collectors.toList())
                .forEach(routes::remove);
        return new Chromosome(
                routes.size(),
                routes
        );
    }
}
