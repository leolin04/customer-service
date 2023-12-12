package com.cat.customerservice.service;

import com.cat.customerservice.MySqlTestBase;
import com.cat.customerservice.api.Customer;
import com.cat.customerservice.entity.CustomerEntity;
import com.cat.customerservice.entity.CustomerType;
import com.cat.customerservice.entity.ServingStatus;
import com.cat.customerservice.repository.CustomerRepository;
import com.cat.customerservice.repository.CustomerServiceCounterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ServiceSchedulerTests extends MySqlTestBase {
    @Autowired
    ServiceScheduler scheduler;

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    public void setup() {
        customerRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCheckIn() {
        Customer customer = new Customer("full name","1718-001-1234", CustomerType.REGULAR);
        Customer checkin = scheduler.checkIn(customer);
        verifyCheckIn(customer, checkin);

        Customer vip = new Customer("vip", "1718-007-1234", CustomerType.VIP);
        Customer vipCheckin = scheduler.checkIn(vip);
        verifyCheckIn(vip, vipCheckin);
    }

    private void verifyCheckIn(Customer customer, Customer checkin) {
        assertTrue(checkin.getTicketId() > 0);
        assertEquals(customer.getName(), checkin.getName());
        assertEquals(customer.getPhoneNumber(), checkin.getPhoneNumber());
        assertEquals(customer.getCustomerType(), checkin.getCustomerType());

        CustomerEntity customerEntity = customerRepository.findById(checkin.getTicketId()).get();
        assertEquals(customer.getName(), customerEntity.getName());
        assertEquals(customer.getPhoneNumber(), customerEntity.getPhoneNumber());
        assertEquals(customer.getCustomerType(), customerEntity.getCustomerType());
        assertEquals(ServingStatus.CHECK_IN, customerEntity.getServingStatus());
    }

    @Test
    @Transactional
    public void testGetNextCustomer() {
        Customer customer = new Customer("full name","1718-001-1234", CustomerType.REGULAR);
        Customer checkin = scheduler.checkIn(customer);

        Customer vip = new Customer("vip", "1718-007-1234", CustomerType.VIP);
        Customer vipCheckin = scheduler.checkIn(vip);

        Customer nextVIPCustomer = scheduler.getNextCustomer().get();
        assertEquals(vipCheckin.getTicketId(), nextVIPCustomer.getTicketId());
        assertEquals(vipCheckin.getName(), nextVIPCustomer.getName());
        assertEquals(vipCheckin.getPhoneNumber(), nextVIPCustomer.getPhoneNumber());
        assertEquals(vipCheckin.getCustomerType(), nextVIPCustomer.getCustomerType());

        Customer nextRegularCustomer = scheduler.getNextCustomer().get();
        assertEquals(checkin.getTicketId(), nextRegularCustomer.getTicketId());
        assertEquals(checkin.getName(), nextRegularCustomer.getName());
        assertEquals(checkin.getPhoneNumber(), nextRegularCustomer.getPhoneNumber());
        assertEquals(checkin.getCustomerType(), nextRegularCustomer.getCustomerType());

        // no more customers
        assertTrue(scheduler.getNextCustomer().isEmpty());
    }

}
