package hr.fer.ztel.hmo.ga.crossover;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.ga.Utils;
import hr.fer.ztel.hmo.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TakeRouteCrossover implements Crossover {
    @Override
    public Pair<Chromosome, Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
        List<Route> parent1Routes = new ArrayList<>();
        parent1.getRoutes().forEach(route -> {
            Route route1 = new Route(new ArrayList<>(route.getCustomerList()));
            parent1Routes.add(route1);
        });
        List<Route> parent2Routes = new ArrayList<>();
        parent2.getRoutes().forEach(route -> {
            Route route1 = new Route(new ArrayList<>(route.getCustomerList()));
            parent2Routes.add(route1);
        });
        Map<Route, Double> parent1Scores = new HashMap<>();
        for (Route r1 : parent1Routes) {
            parent1Scores.put(r1, (double) r1.getCustomerList().size());
        }
        Utils.normalizeScores(parent1Scores, true);
        Route parent1Route = new Route(new ArrayList<>(
                Utils.chooseKFromList(parent1Routes, parent1Scores, 1).get(0).getCustomerList()
        ));
        Map<Route, Double> parent2Scores = new HashMap<>();
        for (Route r : parent2Routes) {
            parent2Scores.put(r, (double) r.getCustomerList().size());
        }
        Utils.normalizeScores(parent2Scores, true);
        Route parent2Route = new Route(new ArrayList<>(
                Utils.chooseKFromList(parent2Routes, parent2Scores, 1).get(0).getCustomerList()
        ));

        parent1Routes.forEach(route -> {
            route.getCustomerList().removeAll(parent2Route.getCustomerList());
        });
        parent1Routes.add(parent2Route);

        parent2Routes.forEach(route -> {
            route.getCustomerList().removeAll(parent1Route.getCustomerList());
        });
        parent2Routes.add(parent1Route);

        parent1Routes
                .stream()
                .filter(route -> route.getCustomerList().size() == 0)
                .collect(Collectors.toList())
                .forEach(parent1Routes::remove);

        parent2Routes
                .stream()
                .filter(route -> route.getCustomerList().size() == 0)
                .collect(Collectors.toList())
                .forEach(parent2Routes::remove);

        Chromosome child1 = new Chromosome(parent1Routes.size(), parent1Routes);
        Chromosome child2 = new Chromosome(parent2Routes.size(), parent2Routes);

        return Pair.of(child1, child2);

    }
}
