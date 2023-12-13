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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ServiceSchedulerIntTests extends MySqlTestBase {
    @Autowired
    ServiceScheduler scheduler;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerServiceCounterRepository counterRepository;

    @BeforeEach
    public void setup() {
        customerRepository.deleteAll();
        counterRepository.deleteAll();
        scheduler.init();
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

    @Test
    public void testGetNextCustomerType() {
        // when both vip and regular are 0
        {
            int vipServed = 0, regularServed = 0;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.VIP, nextCustomerType);
        }

        // when vip is 1, and regular is 0
        {
            int vipServed = 1, regularServed = 0;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.VIP, nextCustomerType);
        }

        // when vip is 2, and regular is 0
        {
            int vipServed = 2, regularServed = 0;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.REGULAR, nextCustomerType);
        }

        // when vip is 0, and regular is 1
        {
            int vipServed = 0, regularServed = 1;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.VIP, nextCustomerType);
        }

        // when vip is 1, and regular is 1
        {
            int vipServed = 1, regularServed = 1;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.VIP, nextCustomerType);
        }

        // when vip is 2, and regular is 1
        {
            int vipServed = 2, regularServed = 1;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.VIP, nextCustomerType);
        }

        // when vip is 3, and regular is 1
        {
            int vipServed = 3, regularServed = 1;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.REGULAR, nextCustomerType);
        }

        // when vip is 1, and regular is 2
        {
            int vipServed = 1, regularServed = 2;
            CustomerType nextCustomerType = scheduler.getNextCustomerType(vipServed, regularServed);
            assertEquals(CustomerType.VIP, nextCustomerType);
        }
    }

    @Test
    public void testGetNextCustomer21() {
        // when no checkins and no customers served
        Optional<Customer> nextCustomerOpt = scheduler.getNextCustomer21();
        assertTrue(nextCustomerOpt.isEmpty());

        // when 2 vip checkins and 1 regular checkin
        Customer vip1 = new Customer("vip1", "1718-007-1234", CustomerType.VIP);
        Customer vipCheckin1 = scheduler.checkIn(vip1);
        Customer vip2 = new Customer("vip2", "1718-007-1234", CustomerType.VIP);
        Customer vipCheckin2 = scheduler.checkIn(vip2);
        Customer regular1 = new Customer("regular 1","1718-001-1234", CustomerType.REGULAR);
        Customer regularCheckin1 = scheduler.checkIn(regular1);

        Customer nextVip1 = scheduler.getNextCustomer21().get();
        assertEquals(vipCheckin1.getTicketId(), nextVip1.getTicketId());
        assertEquals(vipCheckin1.getName(), nextVip1.getName());

        Customer nextVip2 = scheduler.getNextCustomer21().get();
        assertEquals(vipCheckin2.getTicketId(), nextVip2.getTicketId());
        assertEquals(vipCheckin2.getName(), nextVip2.getName());

        Customer nextRegular1 = scheduler.getNextCustomer21().get();
        assertEquals(regularCheckin1.getTicketId(), nextRegular1.getTicketId());
        assertEquals(regularCheckin1.getName(), nextRegular1.getName());

        Optional<Customer> nextCustomer = scheduler.getNextCustomer21();
        assertTrue(nextCustomer.isEmpty());

        // 3rd vip checkin, 2nd regular checkin, and 2 vips served and 1 regular served
        Customer vip3 = new Customer("vip3", "1718-007-1234", CustomerType.VIP);
        Customer vipCheckin3 = scheduler.checkIn(vip3);
        Customer regular2 = new Customer("regular 2","1718-001-1234", CustomerType.REGULAR);
        Customer regularCheckin2 = scheduler.checkIn(regular2);

        Customer nextVip3 = scheduler.getNextCustomer21().get();
        assertEquals(vipCheckin3.getTicketId(), nextVip3.getTicketId());
        assertEquals(vipCheckin3.getName(), nextVip3.getName());

        Customer nextRegular2 = scheduler.getNextCustomer21().get();
        assertEquals(regularCheckin2.getTicketId(), nextRegular2.getTicketId());
        assertEquals(regularCheckin2.getName(), nextRegular2.getName());
    }

}
