package hr.fer.ztel.hmo.parser;

import java.util.Objects;

public class Customer extends Destination {
    private int id;
    private int demand;
    private int serviceTime;

    public Customer(Location location, Window window, int id, int demand, int serviceTime) {
        super(location, window);
        this.id = id;
        this.demand = demand;
        this.serviceTime = serviceTime;
    }

    public int getId() {
        return id;
    }

    public int getDemand() {
        return demand;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", demand=" + demand +
                ", serviceTime=" + serviceTime +
                "} " + super.toString();
    }
}
