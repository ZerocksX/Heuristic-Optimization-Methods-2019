package hr.fer.ztel.hmo.ga;

import hr.fer.ztel.hmo.parser.Customer;

import java.util.List;
import java.util.Objects;

public class Route {
    private List<Customer> customerList;

    public Route(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return customerList.equals(route.customerList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerList);
    }

    @Override
    public String toString() {
        return "Route{" +
                "customerList=" + customerList +
                '}';
    }

    public Customer getLastCustomer() {
        return customerList.isEmpty()
                ? null
                : customerList.get(customerList.size() - 1);
    }
}
