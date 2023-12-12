package com.cat.customerservice.api;

import com.cat.customerservice.entity.CustomerType;

public class Customer {
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
}
