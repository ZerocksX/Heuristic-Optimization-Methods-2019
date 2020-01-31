package hr.fer.ztel.hmo.ga.evauluation;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.Population;
import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.ga.Utils;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.HashMap;

public class EvaluateBySizeThenDistance implements EvaluationFunction {

    private RouteEvaluator routeEvaluator;
    private Instance instance;
    public static final double VEHICLE_SIZE_OFFSET = 1e6;

    public EvaluateBySizeThenDistance(RouteEvaluator routeEvaluator, Instance instance) {
        this.routeEvaluator = routeEvaluator;
        this.instance = instance;
    }

    @Override
    public HashMap<Chromosome, Double> evaluate(Population population) {
        HashMap<Chromosome, Double> scores = new HashMap<>();
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (Chromosome chromosome : population.getChromosomes()) {
            double score = chromosome.getVehicleNumber() * VEHICLE_SIZE_OFFSET;
            for (Route route : chromosome.getRoutes()) {
                RouteEvaluator.RouteEvaluationResult routeEvaluationResult = routeEvaluator.evaluate(route, instance.getDepot());
                score += routeEvaluationResult.getTotalDistance();
            }
            min = Math.min(min, score);
            max = Math.max(max, score);
            scores.put(chromosome, score);
        }
        return scores;
    }
}
