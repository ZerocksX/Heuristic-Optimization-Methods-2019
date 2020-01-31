package hr.fer.ztel.hmo.parser;

import java.util.List;

public class Instance {
    private int vehicleNumber;
    private int vehicleCapacity;
    private Depot depot;
    private List<Customer> customers;
    private int instanceId;

    public Instance(int vehicleNumber, int vehicleCapacity, Depot depot, List<Customer> customers, int instanceId) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleCapacity = vehicleCapacity;
        this.depot = depot;
        this.customers = customers;
        this.instanceId = instanceId;
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public Depot getDepot() {
        return depot;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "vehicleNumber=" + vehicleNumber +
                ", vehicleCapacity=" + vehicleCapacity +
                ", depot=" + depot +
                ", customers=" + customers +
                ", instanceId=" + instanceId +
                '}';
    }
}
