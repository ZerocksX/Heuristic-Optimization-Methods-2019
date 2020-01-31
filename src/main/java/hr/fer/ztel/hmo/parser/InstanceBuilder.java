package hr.fer.ztel.hmo.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InstanceBuilder {


    public static Instance loadSolomonInstance(String type, int instanceId) {

        Path instancePath = null;
        try {
            instancePath = Paths.get(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(String.format("instances/solomon_100/%s%d.TXT", type, instanceId))).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        int code;
        switch (type) {
            case "C":
                code = 1;
                break;
            case "R":
                code = 2;
                break;
            case "RC":
                code = 3;
                break;
            default:
                code = 0;
                break;
        }
        return loadInstance(instancePath, Integer.parseInt(String.valueOf(code) + instanceId), 2);
    }

    public static Instance loadInstance(int instanceId) {
        Path instancePath = null;
        try {
            instancePath = Paths.get(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(String.format("instances/i%d.TXT", instanceId))).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return loadInstance(instancePath, instanceId, 0);
    }

    public static Instance loadInstance(Path instancePath, int instanceId, int skipLines) {
        List<String> lines;
        try {
            lines = Files.readAllLines(instancePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lines = lines.subList(skipLines, lines.size());
        String[] parts = lines.get(2).trim().split("\\s+");
        int vehicleNumber = Integer.parseInt(parts[0].trim());
        int vehicleCapacity = Integer.parseInt(parts[1].trim());
        Depot depot = parseDepot(lines.get(7).trim().split("\\s+"));
        List<Customer> customers = lines.subList(8, lines.size()).stream().map(line -> line.trim().split("\\s+")).map(InstanceBuilder::parseCustomer).collect(Collectors.toList());
        return new Instance(
                vehicleNumber,
                vehicleCapacity,
                depot,
                customers,
                instanceId
        );
    }

    private static Customer parseCustomer(String[] parts) {
        return new Customer(
                new Location(
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim())
                ),
                new Window(
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim())
                ),
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[3].trim()),
                Integer.parseInt(parts[6].trim())
        );
    }

    private static Depot parseDepot(String[] parts) {
        return new Depot(
                new Location(
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim())
                ),
                new Window(
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim())
                )
        );
    }

    public static void main(String[] args) {
        Instance instance = loadInstance(6);
        System.out.println(instance.toString());
    }
}
