package hr.fer.ztel.hmo.ga.mutation;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.ga.Utils;
import hr.fer.ztel.hmo.ga.evauluation.RouteEvaluator;
import hr.fer.ztel.hmo.parser.Customer;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SwapClosestMutation implements Mutation {

    private Instance instance;
    private boolean reverseInsert;
    private boolean fromEnd;
    private boolean shuffle;
    private RouteEvaluator routeEvaluator = new RouteEvaluator();

    public SwapClosestMutation(Instance instance, boolean shuffle) {
        this(instance, false, false, shuffle);
    }

    public SwapClosestMutation(Instance instance, boolean reverseInsert, boolean fromEnd) {
        this(instance, reverseInsert, fromEnd, false);
    }

    public SwapClosestMutation(Instance instance, boolean reverseInsert, boolean fromEnd, boolean shuffle) {
        this.instance = instance;
        this.reverseInsert = reverseInsert;
        this.fromEnd = fromEnd;
        this.shuffle = shuffle;
    }

    @Override
    public Chromosome mutate(Chromosome original) {
        List<Route> routes = new LinkedList<>();
        original.getRoutes().forEach(r -> routes.add(new Route(new LinkedList<>(r.getCustomerList()))));
        Map<Route, RouteEvaluator.RouteEvaluationResult> results = new HashMap<>();
        for (Route r1 : routes) {
            results.put(r1, routeEvaluator.evaluate(r1, instance.getDepot()));
        }
        Route emptiestRoute = routes.stream().min(Comparator.<Route>comparingInt(r -> results.get(r).getTotalCapacityUsed())).get();
        Customer farthestCustomer = emptiestRoute.getCustomerList().stream().max(Comparator.comparingDouble(c -> emptiestRoute.getCustomerList().stream().mapToDouble(o -> o.getLocation().distanceTo(c.getLocation())).max().getAsDouble())).get();
        List<Route> candidates = routes.stream().filter(r -> results.get(r).getTotalCapacityUsed() <= instance.getVehicleCapacity() - farthestCustomer.getDemand()).filter(route -> !route.equals(emptiestRoute)).collect(Collectors.toList());
        while (!candidates.isEmpty()) {
            Route candidate = candidates.get(0);
            candidates.remove(0);
            List<Integer> indexes = new LinkedList<>();
            for (int i = 0; i <= candidate.getCustomerList().size(); i++) {
                indexes.add(i);
            }
            boolean found = false;
            Collections.shuffle(indexes);
            for (Integer i : indexes) {
                List<Customer> customers = new ArrayList<>(candidate.getCustomerList());
                customers.add(i, farthestCustomer);
                Route newRoute = new Route(customers);
                if (Utils.validateRoute(routeEvaluator, newRoute, instance)) {
                    found = true;
                    candidate.getCustomerList().add(i, farthestCustomer);
                    if(emptiestRoute.getCustomerList().size()==1){
                        routes.remove(emptiestRoute);
                    } else {
                        emptiestRoute.getCustomerList().remove(farthestCustomer);
                    }
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        return new Chromosome(routes.size(), routes);
    }
}
