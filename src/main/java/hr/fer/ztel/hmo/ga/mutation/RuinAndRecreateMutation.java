package hr.fer.ztel.hmo.ga.mutation;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.ga.Utils;
import hr.fer.ztel.hmo.ga.evauluation.CachingEvaluationFunction;
import hr.fer.ztel.hmo.ga.evauluation.EvaluateBySizeThenDistance;
import hr.fer.ztel.hmo.ga.evauluation.RouteEvaluator;
import hr.fer.ztel.hmo.parser.Customer;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RuinAndRecreateMutation implements Mutation {

    private Instance instance;
    private boolean reverseInsert;
    private boolean fromEnd;
    private boolean shuffle;

    public RuinAndRecreateMutation(Instance instance, boolean shuffle) {
        this(instance, false, false, shuffle);
    }

    public RuinAndRecreateMutation(Instance instance, boolean reverseInsert, boolean fromEnd) {
        this(instance, reverseInsert, fromEnd, false);
    }

    public RuinAndRecreateMutation(Instance instance, boolean reverseInsert, boolean fromEnd, boolean shuffle) {
        this.instance = instance;
        this.reverseInsert = reverseInsert;
        this.fromEnd = fromEnd;
        this.shuffle = shuffle;
    }

    @Override
    public Chromosome mutate(Chromosome original) {
        List<Route> routes = new LinkedList<>();
        original.getRoutes().forEach(r -> routes.add(new Route(new LinkedList<>(r.getCustomerList()))));
        Map<Route, Double> scores = new HashMap<>();
//        routes.forEach(route -> scores.put(route, (double) route.getCustomerList().size()));
//        Utils.normalizeScores(scores, true);
//        routes.forEach(route -> scores.put(route, route.getCustomerList().stream().mapToDouble(Customer::getDemand).sum() + EvaluateBySizeThenDistance.VEHICLE_SIZE_OFFSET * route.getCustomerList().size()));
        routes.forEach(route -> scores.put(route, 1.0));
        Utils.normalizeScores(scores, false);
        List<Customer> customers;
        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            List<Route> chosenRoutes = Utils.chooseKFromList(routes, scores, ThreadLocalRandom.current().nextInt(2) + 1);
            customers = chosenRoutes.stream().flatMap(route -> route.getCustomerList().stream()).collect(Collectors.toCollection(LinkedList::new));
            routes.removeAll(chosenRoutes);
        } else {
            List<Route> chosenRoutes = Utils.chooseKFromList(routes, scores, ThreadLocalRandom.current().nextInt(Math.min(7, routes.size())) + 1);
            List<Customer> cs = chosenRoutes.stream().flatMap(r -> r.getCustomerList().stream().filter(c -> ThreadLocalRandom.current().nextDouble() < 0.3)).collect(Collectors.toList());
            routes.stream()
                    .filter(route -> route.getCustomerList().size() == 0)
                    .collect(Collectors.toList())
                    .forEach(routes::remove);
            chosenRoutes.forEach(r -> r.getCustomerList().removeAll(cs));
            customers = cs;
        }

        Comparator<Customer> comparator = Comparator.comparingDouble(customer -> customer.getWindow().getDue());
        comparator = this.reverseInsert ? comparator.reversed() : comparator;
        if (shuffle) {
            Collections.shuffle(customers);
        } else {
            customers.sort(comparator);
        }
//        customers.forEach(c -> Utils.insertCustomerIntoRoutes(routes, instance, new RouteEvaluator(), c, this.fromEnd, this.shuffle));
        double rand = ThreadLocalRandom.current().nextDouble();
        if (rand < 1) {
            customers.forEach(c -> Utils.insertCustomerIntoRoutes(routes, instance, new RouteEvaluator(), c, this.fromEnd, this.shuffle));
        } else if (rand < 0) {
            customers.forEach(c -> Utils.insertCustomerIntoRoutesAtEnd(routes, instance, new RouteEvaluator(), c));
        } else {
            customers.forEach(c -> Utils.insertCustomerIntoRoutesMinDist(routes, instance, new RouteEvaluator(), c));
        }
        return new Chromosome(routes.size(), routes);
    }
}
