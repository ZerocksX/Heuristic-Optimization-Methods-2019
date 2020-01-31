package hr.fer.ztel.hmo;

import hr.fer.ztel.hmo.ga.Chromosome;
import hr.fer.ztel.hmo.ga.GeneticAlgorithm;
import hr.fer.ztel.hmo.ga.Population;
import hr.fer.ztel.hmo.ga.Route;
import hr.fer.ztel.hmo.ga.crossover.IdentityCrossover;
import hr.fer.ztel.hmo.ga.crossover.TakeRouteCrossover;
import hr.fer.ztel.hmo.ga.evauluation.*;
import hr.fer.ztel.hmo.ga.initialization.RandomInitializer;
import hr.fer.ztel.hmo.ga.mutation.RuinAndRecreateMutation;
import hr.fer.ztel.hmo.ga.mutation.SwapClosestMutation;
import hr.fer.ztel.hmo.ga.selection.TournamentSelection;
import hr.fer.ztel.hmo.parser.Instance;
import hr.fer.ztel.hmo.parser.InstanceBuilder;
import hr.fer.ztel.hmo.util.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static RouteEvaluator routeEvaluator = new RouteEvaluator();

    public static void main(String[] args) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String folder = dtf.format(LocalDateTime.now());
        if (args.length == 1 && args[0].equals("solomon")) {
            List<Pair<String, Integer>> instances = Arrays.asList(Pair.of("C", 101), Pair.of("C", 201), Pair.of("R", 101), Pair.of("R", 108), Pair.of("R", 201), Pair.of("R", 211), Pair.of("RC", 105), Pair.of("RC", 204));
            for (Pair<String, Integer> instance : instances) {
                Path folderPath = Paths.get("runs", folder);
                Files.createDirectories(folderPath);
                solomon(instance.getFirst(), instance.getSecond(), folderPath.toAbsolutePath().toString());
            }
        } else {
            for (Integer i : Arrays.asList(7)) {
                Path folderPath = Paths.get("runs", folder, String.valueOf(i));
                Files.createDirectories(folderPath);
                run(i, folderPath.toAbsolutePath().toString());
            }
        }
    }

    public static void solomon(String type, int instanceId, String folder) throws IOException {
        Instance instance = InstanceBuilder.loadSolomonInstance(type, instanceId);
        CachingEvaluationFunction cachingEvaluationFunction = new CachingEvaluationFunction(new EvaluateBySizeThenDistance(routeEvaluator, instance));
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new Population(10, new RandomInitializer(0, 0, instance, routeEvaluator)),
                Arrays.asList(
                        new TakeRouteCrossover(),
                        new IdentityCrossover()
                ),
                Arrays.asList(
//                        new RuinAndRecreateMutation(instance, true, true),
//                        new RuinAndRecreateMutation(instance, true, false),
//                        new RuinAndRecreateMutation(instance, false, true),
//                        new RuinAndRecreateMutation(instance, false, false),
                        new RuinAndRecreateMutation(instance, true)
                ),
                new TournamentSelection(5),
                instance,
                new NormalizingEvaluationFunction(cachingEvaluationFunction)
        );
        EvaluationFunction evaluator = new EvaluateBySizeThenDistance(new RouteEvaluator(), instance);
        log(ga, evaluator);
        Integer i = 1;
        iterate(1, "1m", i, 1000, instance, ga, evaluator, folder, cachingEvaluationFunction);
        iterate(4, "5m", i, 5000, instance, ga, evaluator, folder, cachingEvaluationFunction);
        iterate(5, "un", i, 10000, instance, ga, evaluator, folder, cachingEvaluationFunction);
    }

    public static void run(int instanceId, String folder) throws IOException {
        Instance instance = InstanceBuilder.loadInstance(instanceId);
        CachingEvaluationFunction cachingEvaluationFunction = new CachingEvaluationFunction(new EvaluateBySizeThenDistance(routeEvaluator, instance));
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new Population(10, new RandomInitializer(5   ,7, instance, routeEvaluator)),
                Arrays.asList(
//                        new TakeRouteCrossover()
                        new TakeRouteCrossover(),
                        new IdentityCrossover()
                ),
                Arrays.asList(
//                        new RuinAndRecreateMutation(instance, true, true),
//                        new RuinAndRecreateMutation(instance, true, false),
//                        new RuinAndRecreateMutation(instance, false, true),
                        new RuinAndRecreateMutation(instance, false, false),
                        new RuinAndRecreateMutation(instance, true)
//        new SwapClosestMutation(instance, false)
                ),
                new TournamentSelection(5),
                instance,
                new NormalizingEvaluationFunction(cachingEvaluationFunction)
        );
        EvaluationFunction evaluator = new EvaluateBySizeThenDistance(new RouteEvaluator(), instance);
        log(ga, evaluator);
        Integer i = 1;
        iterate(1, "1m", i, Integer.MAX_VALUE, instance, ga, evaluator, folder, cachingEvaluationFunction);
        iterate(4, "5m", i, Integer.MAX_VALUE, instance, ga, evaluator, folder, cachingEvaluationFunction);
        iterate(3, "un", i, Integer.MAX_VALUE, instance, ga, evaluator, folder, cachingEvaluationFunction);
    }

    public static void iterate(double minutes, String time, Integer i, int limit, Instance instance, GeneticAlgorithm ga, EvaluationFunction evaluator, String folder, CachingEvaluationFunction cachingEvaluationFunction) throws IOException {
        long start = System.currentTimeMillis();
        for (; i <= limit && System.currentTimeMillis() - start < minutes * 60 * 1000; i++) {
            ga.iterate(evaluator);
            if (i % 100 == 0) {
                log(ga, evaluator, i);
            }
        }
        log(ga, evaluator);
        writeResult(instance, ga, time, folder, cachingEvaluationFunction.getCalls());
    }

    public static void log(GeneticAlgorithm ga, EvaluationFunction evaluator) {
        log(ga, evaluator, null);
    }

    public static void log(GeneticAlgorithm ga, EvaluationFunction evaluator, Integer round) {
        HashMap<Chromosome, Double> evaluation = evaluator.evaluate(ga.getPopulation());
        Chromosome best = evaluation.keySet().stream().min(Comparator.comparingDouble(evaluation::get)).get();
        if (round == null) {
            System.out.println("Best: ");
        } else {
            System.out.println(String.format("Round %d: ", round));
        }
        System.out.println(String.format("\tVehicles: %d", best.getVehicleNumber()));
        System.out.println(String.format("\tScore: %.2f", evaluation.get(best) - best.getVehicleNumber() * EvaluateBySizeThenDistance.VEHICLE_SIZE_OFFSET));
    }

    public static void writeResult(Instance instance, GeneticAlgorithm ga, String time, String folder, int calls) throws IOException {
//        HashMap<Chromosome, Double> evaluation = evaluator.evaluate(ga.getPopulation());
//        Chromosome best = evaluation.keySet().stream().min(Comparator.<Chromosome>comparingDouble(evaluation::get)).get();
//        double score = evaluation.get(best);
        Chromosome best = ga.getBest();
        double score = ga.getBestScore();
        Path results = Paths.get(folder, "res-" + time + "-i" + instance.getInstanceId() + ".txt");
        Files.deleteIfExists(results);
        BufferedWriter writer = Files.newBufferedWriter(results, StandardOpenOption.CREATE_NEW);
        writer.write(String.valueOf(best.getVehicleNumber()));
        writer.newLine();
        for (int i = 0; i < best.getRoutes().size(); i++) {
            Route route = best.getRoutes().get(i);
            writer.write(String.valueOf(i + 1));
            writer.write(": ");
            List<Integer> serviceTimes = new RouteEvaluator().calculateServiceTimes(route, instance.getDepot());
            for (int k = 0; k < serviceTimes.size(); k++) {
                if (k == 0 || k == serviceTimes.size() - 1) {
                    writer.write(String.valueOf(0));
                } else {
                    writer.write(String.valueOf(route.getCustomerList().get(k - 1).getId()));
                }
                writer.write(String.format("(%d)", serviceTimes.get(k)));
                if (k != serviceTimes.size() - 1) {
                    writer.write("->");
                }
            }
            writer.newLine();
        }
        writer.write(String.format("%.2f", score));
        writer.newLine();
        writer.write(String.format("%d", calls));
        writer.close();
    }
}
