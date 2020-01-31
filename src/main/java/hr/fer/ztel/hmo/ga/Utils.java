package hr.fer.ztel.hmo.ga;

import hr.fer.ztel.hmo.ga.evauluation.RouteEvaluator;
import hr.fer.ztel.hmo.parser.Customer;
import hr.fer.ztel.hmo.parser.Destination;
import hr.fer.ztel.hmo.parser.Instance;

import java.util.*;

public class Utils {
    public static final Random random = new Random();

    public static void insertCustomerIntoRoutesAtEnd(List<Route> routes, Instance instance, RouteEvaluator routeEvaluator, Customer customer) {
        int vehicleToAddTo = -1;
        List<Integer> vehicles = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            vehicles.add(i);
        }
        while (!vehicles.isEmpty()) {
            int vehicle = vehicles.get(random.nextInt(vehicles.size()));
            vehicles.remove(Integer.valueOf(vehicle));
            Route route = routes.get(vehicle);
            RouteEvaluator.RouteEvaluationResult result = routeEvaluator.evaluate(route, instance.getDepot(), false);
            if (instance.getVehicleCapacity() - result.getTotalCapacityUsed() < customer.getDemand()) {
                continue;
            }
            Customer c = route.getLastCustomer();
            Destination previous = c == null
                    ? instance.getDepot()
                    : c;
            double distanceToCustomer = previous.getLocation().distanceTo(customer.getLocation());
            int totalTime = result.getTotalTime() + (int) Math.ceil(distanceToCustomer);
            if (totalTime > customer.getWindow().getDue()) {
                continue;
            }
            if (totalTime < customer.getWindow().getReady()) {
                totalTime = customer.getWindow().getReady();
            }
            double distanceToDepot = customer.getLocation().distanceTo(instance.getDepot().getLocation());
            totalTime += Math.ceil(distanceToDepot);
            if (totalTime <= instance.getDepot().getWindow().getDue()) {
                vehicleToAddTo = vehicle;
                break;
            }
        }
        if (vehicleToAddTo >= 0) {
            routes.get(vehicleToAddTo).getCustomerList().add(customer);
        } else {
            routes.add(new Route(new LinkedList<>(Collections.singletonList(customer))));
        }
    }

    public static MaxSizeHashMap<Route, RouteEvaluator.RouteEvaluationResult> cache = new MaxSizeHashMap<Route, RouteEvaluator.RouteEvaluationResult>(10_000);

    public static void insertCustomerIntoRoutes(List<Route> routes, Instance instance, RouteEvaluator routeEvaluator, Customer customer, boolean fromEnd, boolean shuffle) {
        int vehicleToAddTo = -1;
        List<Integer> vehicles = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            vehicles.add(i);
        }
        Collections.shuffle(vehicles);
        vehicles = vehicles.subList(0, Math.min(15, vehicles.size()));
        while (!vehicles.isEmpty()) {
            int vehicle = vehicles.get(0);
            vehicles.remove(0);
            Route route = routes.get(vehicle);
            RouteEvaluator.RouteEvaluationResult result = cache.computeIfAbsent(route, route1 -> routeEvaluator.evaluate(route1, instance.getDepot(), false));
            cache.putIfAbsent(route, result);
            if (instance.getVehicleCapacity() - result.getTotalCapacityUsed() < customer.getDemand()) {
                continue;
            }
            List<Integer> indexes = new LinkedList<>();
            for (int i = fromEnd ? route.getCustomerList().size() : 0; fromEnd ? i >= 0 : i <= route.getCustomerList().size(); ) {
                indexes.add(i);
                if (fromEnd) {
                    i--;
                } else {
                    i++;
                }
            }
            if (shuffle) {
                Collections.shuffle(indexes);
            }
            indexes = indexes.subList(0, Math.min(50, indexes.size()));
            for (Integer i : indexes) {
                List<Customer> customers = new ArrayList<>(route.getCustomerList());
                customers.add(i, customer);
                Route newRoute = new Route(customers);
                List<Destination> destinations = new ArrayList<>(newRoute.getCustomerList());
                destinations.add(0, instance.getDepot());
                destinations.add(destinations.size(), instance.getDepot());
                List<Integer> serviceTimes = routeEvaluator.calculateServiceTimes(newRoute, instance.getDepot());
                boolean valid = true;
                for (int j = 0; j < destinations.size(); j++) {
                    Integer serviceTime = serviceTimes.get(j);
                    if (!(destinations.get(j).getWindow().getReady() <= serviceTime && destinations.get(j).getWindow().getDue() >= serviceTime)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    vehicleToAddTo = vehicle;
                    routes.get(vehicleToAddTo).getCustomerList().add(i, customer);
                    break;
                }
            }
            if (vehicleToAddTo != -1) {
                break;
            }
        }
        if (vehicleToAddTo == -1) {
            routes.add(new Route(new LinkedList<>(Collections.singletonList(customer))));
        }
    }

    public static void insertCustomerIntoRoutesMinDist(List<Route> routes, Instance instance, RouteEvaluator routeEvaluator, Customer customer) {
        int vehicleToAddTo = -1;
        LinkedList<Integer> vehicles = new LinkedList<>();
        Map<Integer, Double> distances = new HashMap<>();
        for (int i = 0; i < routes.size(); i++) {
            RouteEvaluator.RouteEvaluationResult result = routeEvaluator.evaluate(routes.get(i), instance.getDepot(), false);
            if (instance.getVehicleCapacity() - result.getTotalCapacityUsed() < customer.getDemand()) {
                continue;
            }
            vehicles.add(i);
        }
        for (Integer vehicle : vehicles) {
            double minDist = 0;
            for (int i = 1; i < routes.get(vehicle).getCustomerList().size(); i++) {
                double dist = 0;
                Customer cPrev = routes.get(vehicle).getCustomerList().get(i - 1);
                dist += cPrev.getLocation().distanceTo(customer.getLocation());
                Customer cNext = routes.get(vehicle).getCustomerList().get(i);
                dist += cNext.getLocation().distanceTo(customer.getLocation());
//                dist -= cPrev.getLocation().distanceTo(cNext.getLocation());
                minDist = Math.min(minDist, dist);
            }
            distances.put(vehicle, minDist);
        }
        Utils.normalizeScores(distances, true);
        vehicles = new LinkedList<>(Utils.chooseKFromList(vehicles, distances, vehicles.size()));
//        vehicles.sort(Comparator.<Integer>comparingDouble(distances::get));
        while (!vehicles.isEmpty()) {
            int vehicle = vehicles.getFirst();
            vehicles.removeFirst();
            Route route = routes.get(vehicle);
            List<Integer> indexes = new LinkedList<>();
            for (int i = 0; i <= route.getCustomerList().size(); i++) {
                indexes.add(i);
            }
            Collections.shuffle(indexes);
            for (Integer i : indexes) {
                List<Customer> customers = new ArrayList<>(route.getCustomerList());
                customers.add(i, customer);
                Route newRoute = new Route(customers);
                if (validateRoute(routeEvaluator, newRoute, instance)) {
                    vehicleToAddTo = vehicle;
                    routes.get(vehicleToAddTo).getCustomerList().add(i, customer);
                }
            }
            if (vehicleToAddTo != -1) {
                break;
            }
        }
        if (vehicleToAddTo == -1) {
            routes.add(new Route(new LinkedList<>(Collections.singletonList(customer))));
        }
    }


    public static boolean validateRoute(RouteEvaluator routeEvaluator, Route newRoute, Instance instance) {
        List<Destination> destinations = new ArrayList<>(newRoute.getCustomerList());
        destinations.add(0, instance.getDepot());
        destinations.add(destinations.size(), instance.getDepot());
        List<Integer> serviceTimes = routeEvaluator.calculateServiceTimes(newRoute, instance.getDepot());
        boolean valid = true;
        for (int j = 0; j < destinations.size(); j++) {
            Integer serviceTime = serviceTimes.get(j);
            if (!(destinations.get(j).getWindow().getReady() <= serviceTime && destinations.get(j).getWindow().getDue() >= serviceTime)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    public static <T> void normalizeScores(Map<T, Double> scores, boolean reverse) {
        int n = scores.keySet().size();
        double delta = 100.0 / ((n * (n + 1)) / 2.0);
        List<T> orderedKeys = new ArrayList<T>(scores.keySet());
        Comparator<T> comparator = Comparator.comparingDouble(scores::get);
        comparator = reverse ? comparator.reversed() : comparator;
        orderedKeys.sort(comparator);
        for (int i = 0; i < orderedKeys.size(); i++) {
            scores.put(orderedKeys.get(i), (i + 1) * delta);
        }
    }

    public static <T> List<T> chooseKFromList(List<T> elements, Map<T, Double> scores, int k) {
        double sum = elements.stream().mapToDouble(scores::get).sum();
        List<T> picked = new ArrayList<>();
        List<T> elementsCopy = new ArrayList<>(elements);
        for (int i = 0; i < k; i++) {
            double rand = random.nextDouble() * sum;
            double current = 0;
            T pick = null;
            for (T element : elementsCopy) {
                current += scores.get(element);
                if (current >= rand) {
                    pick = element;
                    break;
                }
            }
            picked.add(pick);
            sum -= scores.get(pick);
            elementsCopy.remove(pick);
        }
        picked.sort(Comparator.<T>comparingDouble(scores::get).reversed());
        return picked;
    }
}
