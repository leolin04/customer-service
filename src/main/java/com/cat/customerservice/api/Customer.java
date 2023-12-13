package com.cat.customerservice.api;

import com.cat.customerservice.entity.CustomerType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Customer {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int ticketId;

    private String name;

    private String phoneNumber;

    private CustomerType customerType;

    public Customer() {
    }

    public Customer(String name, String phoneNumber, CustomerType customerType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.customerType = customerType;
    }

    public Customer(int ticketId, String name, String phoneNumber, CustomerType customerType) {
        this.ticketId = ticketId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.customerType = customerType;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (ticketId != customer.ticketId) return false;
        if (!Objects.equals(name, customer.name)) return false;
        if (!Objects.equals(phoneNumber, customer.phoneNumber))
            return false;
        return customerType == customer.customerType;
    }

    @Override
    public int hashCode() {
        int result = ticketId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (customerType != null ? customerType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Customer{");
        sb.append("ticketId=").append(ticketId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", customerType=").append(customerType);
        sb.append('}');
        return sb.toString();
    }
}
