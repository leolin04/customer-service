package com.cat.customerservice.repository;


import com.cat.customerservice.MySqlTestBase;
import com.cat.customerservice.entity.CustomerServiceCounterEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerServiceCounterEntityRepositoryIntTests extends MySqlTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceCounterEntityRepositoryIntTests.class);

    @Autowired
    private CustomerServiceCounterRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

    @Test
    public void testCreate() {
        CustomerServiceCounterEntity serviceCounter = new CustomerServiceCounterEntity(1, 2, 0);
        repository.save(serviceCounter);
        CustomerServiceCounterEntity savedCounter = repository.findById(serviceCounter.getId()).get();
        assertEquals(2, serviceCounter.getVipServedCount());
        assertEquals(0, savedCounter.getRegularServedCount());
    }

    @Test
    public void testUpdate() {
        CustomerServiceCounterEntity serviceCounter = new CustomerServiceCounterEntity(1, 2, 0);
        repository.save(serviceCounter);

        CustomerServiceCounterEntity savedCounter = repository.findById(serviceCounter.getId()).get();
        savedCounter.setVipServedCount(3);
        savedCounter.setRegularServedCount(1);
        repository.save(savedCounter);

        CustomerServiceCounterEntity updated = repository.findById(serviceCounter.getId()).get();
        assertEquals(3,updated.getVipServedCount());
        assertEquals(1, updated.getRegularServedCount());
    }

    @Test
    public void testIncreaseCounters() {
        CustomerServiceCounterEntity serviceCounter = new CustomerServiceCounterEntity(1, 0, 0);
        repository.save(serviceCounter);
        CustomerServiceCounterEntity saved = repository.findById(serviceCounter.getId()).get();

        {
          repository.increaseVIPCounter(saved.getId());
          CustomerServiceCounterEntity updated = repository.findById(serviceCounter.getId()).get();
          assertEquals(1, updated.getVipServedCount());
        }

        {
          repository.increaseRegularCounter(saved.getId());
          CustomerServiceCounterEntity updated = repository.findById(serviceCounter.getId()).get();
          assertEquals(1, updated.getRegularServedCount());
        }
    }
}
