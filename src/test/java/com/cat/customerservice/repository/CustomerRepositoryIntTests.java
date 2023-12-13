package com.cat.customerservice.repository;


import com.cat.customerservice.MySqlTestBase;
import com.cat.customerservice.entity.CustomerEntity;
import com.cat.customerservice.entity.CustomerType;
import com.cat.customerservice.entity.ServingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryIntTests extends MySqlTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerRepositoryIntTests.class);

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

    @Test
    public void testCreate() {
        CustomerEntity customerEntity = new CustomerEntity(
                "full name",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.CHECK_IN);

        repository.save(customerEntity);

        CustomerEntity foundCustomerEntity = repository.findById(customerEntity.getId()).get();
        assertEquals(customerEntity.getName(), foundCustomerEntity.getName());
        assertEquals(customerEntity.getPhoneNumber(), foundCustomerEntity.getPhoneNumber());
        assertEquals(1, repository.count());
    }

    @Test
    public void testUpdate() {
        CustomerEntity customerEntity = new CustomerEntity(
                "full name",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.CHECK_IN);

        repository.save(customerEntity);

        CustomerEntity savedCustomerEntity = repository.findById(customerEntity.getId()).get();
        savedCustomerEntity.setServingStatus(ServingStatus.SERVED);
        repository.save(savedCustomerEntity);
        CustomerEntity updatedCustomerEntity = repository.findById(customerEntity.getId()).get();
        assertEquals(savedCustomerEntity.getId(), updatedCustomerEntity.getId());
        assertEquals(savedCustomerEntity.getName(), updatedCustomerEntity.getName());
        assertEquals(ServingStatus.SERVED, updatedCustomerEntity.getServingStatus());
    }

    @Test
    public void testFindByCustomerTypeAndStatus_Checkin() {
        CustomerEntity customerEntity1 = new CustomerEntity(
                "full name 1",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.CHECK_IN);

        CustomerEntity customerEntity2 = new CustomerEntity(
                "full name 2",
                "1718-001-1235",
                CustomerType.VIP,
                new Date(),
                ServingStatus.CHECK_IN);
        repository.save(customerEntity1);
        repository.save(customerEntity2);

        CustomerEntity foundCustomerEntity = repository.findTopByCustomerTypeAndServingStatusOrderByCheckInTimeAsc(
                CustomerType.REGULAR, ServingStatus.CHECK_IN).get();

        assertEquals(customerEntity1.getId(), foundCustomerEntity.getId());
        assertEquals(customerEntity1.getName(), foundCustomerEntity.getName());
        assertEquals(customerEntity1.getCustomerType(), foundCustomerEntity.getCustomerType());
        assertEquals(ServingStatus.CHECK_IN, customerEntity1.getServingStatus());
    }

    @Test
    public void testFindNextCheckinCustomerByType() {
        CustomerEntity customerEntity1 = new CustomerEntity(
                "full name 1",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.CHECK_IN);

        CustomerEntity customerEntity2 = new CustomerEntity(
                "full name 2",
                "1718-001-1235",
                CustomerType.VIP,
                new Date(),
                ServingStatus.CHECK_IN);
        repository.save(customerEntity1);
        repository.save(customerEntity2);

        CustomerEntity foundCustomerEntity = repository.findNextCheckinCustomerByType(CustomerType.REGULAR).get();

        assertEquals(customerEntity1.getId(), foundCustomerEntity.getId());
        assertEquals(customerEntity1.getName(), foundCustomerEntity.getName());
        assertEquals(customerEntity1.getCustomerType(), foundCustomerEntity.getCustomerType());
        assertEquals(ServingStatus.CHECK_IN, customerEntity1.getServingStatus());
    }

    @Test
    public void testFindByCustomerTypeAndStatus_Served() {
        CustomerEntity customerEntity1 = new CustomerEntity(
                "full name 1",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.SERVED);

        CustomerEntity customerEntity2 = new CustomerEntity(
                "full name 2",
                "1718-001-1235",
                CustomerType.VIP,
                new Date(),
                ServingStatus.SERVED);
        repository.save(customerEntity1);
        repository.save(customerEntity2);

        CustomerEntity foundCustomerEntity = repository.findTopByCustomerTypeAndServingStatusOrderByCheckInTimeAsc(
                CustomerType.REGULAR, ServingStatus.SERVED).get();

        assertEquals(customerEntity1.getId(), foundCustomerEntity.getId());
        assertEquals(customerEntity1.getName(), foundCustomerEntity.getName());
        assertEquals(customerEntity1.getCustomerType(), foundCustomerEntity.getCustomerType());
        assertEquals(ServingStatus.SERVED, customerEntity1.getServingStatus());
    }

    @Test
    public void testFindByCustomerTypeAndStatus_Checkin_VIP() {
        CustomerEntity regular = new CustomerEntity(
                "full name 1",
                "1718-001-1234",
                CustomerType.REGULAR,
                new Date(),
                ServingStatus.CHECK_IN);

        CustomerEntity vip = new CustomerEntity(
                "full name 2",
                "1718-001-1235",
                CustomerType.VIP,
                new Date(),
                ServingStatus.CHECK_IN);
        repository.save(regular);
        repository.save(vip);

        CustomerEntity foundCustomerEntity = repository.findTopByCustomerTypeAndServingStatusOrderByCheckInTimeAsc(
                CustomerType.VIP, ServingStatus.CHECK_IN).get();

        assertEquals(vip.getId(), foundCustomerEntity.getId());
        assertEquals(vip.getName(), foundCustomerEntity.getName());
        assertEquals(vip.getCustomerType(), foundCustomerEntity.getCustomerType());
        assertEquals(ServingStatus.CHECK_IN, vip.getServingStatus());
    }
}
