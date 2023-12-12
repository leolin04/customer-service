package com.cat.customerservice.service;

import com.cat.customerservice.api.Customer;
import com.cat.customerservice.entity.CustomerEntity;
import com.cat.customerservice.entity.CustomerType;
import com.cat.customerservice.entity.ServingStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerMapperTests {

    private  CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    public void testApiToEntity() {
        assertNotNull(mapper);

        Customer customer = new Customer("full name", "1718-001-1234", CustomerType.REGULAR);
        CustomerEntity entity = mapper.apiToEntity(customer);
        assertEquals(customer.getName(), entity.getName());
        assertEquals(customer.getPhoneNumber(), entity.getPhoneNumber());
        assertEquals(customer.getCustomerType(), entity.getCustomerType());
    }

    @Test
    public void testEntityToApi() {
        assertNotNull(mapper);

        CustomerEntity entity = new CustomerEntity(
                "full name",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.CHECK_IN);
        entity.setId(1);
        Customer customer = mapper.entityToApi(entity);

        assertEquals(entity.getId(), customer.getTicketId());
        assertEquals(entity.getName(), customer.getName());
        assertEquals(entity.getPhoneNumber(), customer.getPhoneNumber());
        assertEquals(entity.getCustomerType(), customer.getCustomerType());
    }
}
