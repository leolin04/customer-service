package com.cat.customerservice.service;

import com.cat.customerservice.api.Customer;
import com.cat.customerservice.entity.CustomerEntity;
import com.cat.customerservice.entity.CustomerServiceCounterEntity;
import com.cat.customerservice.entity.CustomerType;
import com.cat.customerservice.entity.ServingStatus;
import com.cat.customerservice.repository.CustomerRepository;
import com.cat.customerservice.repository.CustomerServiceCounterRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class ServiceScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceScheduler.class);

    private final CustomerRepository customerRepository;

    private final CustomerServiceCounterRepository counterRepository;

    private final CustomerMapper customerMapper;

    public final static int COUNTER_ID = 1;

    @Autowired
    public ServiceScheduler(CustomerRepository customerRepository,
                            CustomerServiceCounterRepository counterRepository,
                            CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.counterRepository = counterRepository;
        this.customerMapper = customerMapper;
    }

    @PostConstruct
    @Transactional
    public void init() {
        try {
            Optional<CustomerServiceCounterEntity> counterEntityOpt = counterRepository.findById(COUNTER_ID);
            if (counterEntityOpt.isEmpty()) {
                LOG.debug("Initializing the CustomerServiceCounter with Id {}", COUNTER_ID);
                CustomerServiceCounterEntity counterEntity = new CustomerServiceCounterEntity(COUNTER_ID, 0, 0);
                counterRepository.save(counterEntity);
                LOG.debug("The CustomerServiceCounter with Id {} is successfully initialized", COUNTER_ID);
            }
        } catch (DuplicateKeyException duEx) {
            LOG.info("CustomerServiceCounter with Id {} is already existed", COUNTER_ID);
        }
    }

    @Transactional
    public Customer checkIn(Customer customer) {
        CustomerEntity customerEntity = customerMapper.apiToEntity(customer);
        customerEntity.setServingStatus(ServingStatus.CHECK_IN);
        customerRepository.save(customerEntity);
        return customerMapper.entityToApi(customerEntity);
    }

    /**
     * Serve VIP Customers first, then Regular Customers
      */
    @Transactional
    public Optional<Customer> getNextCustomer() {
        // try to serve a vip customer first
        Optional<CustomerEntity> vipOpt = customerRepository
                .findTopByCustomerTypeAndServingStatusOrderByCheckInTimeAsc(CustomerType.VIP, ServingStatus.CHECK_IN);
        if (vipOpt.isPresent()) {
            CustomerEntity customerEntity = vipOpt.get();
            customerEntity.setServingStatus(ServingStatus.SERVED);
            customerRepository.save(customerEntity);
            increaseCounter(CustomerType.VIP);
            return Optional.of(customerMapper.entityToApi(vipOpt.get()));
        }

        // try to serve a regular customer since no more vips
        Optional<CustomerEntity> regularOpt = customerRepository
                .findTopByCustomerTypeAndServingStatusOrderByCheckInTimeAsc(CustomerType.REGULAR, ServingStatus.CHECK_IN);
        if (regularOpt.isPresent()) {
            CustomerEntity customerEntity = regularOpt.get();
            customerEntity.setServingStatus(ServingStatus.SERVED);
            customerRepository.save(customerEntity);
            increaseCounter(CustomerType.REGULAR);
            return Optional.of(customerMapper.entityToApi(regularOpt.get()));
        }

        return Optional.empty();
    }

    // Server VIP and Regular Customers with the VIP : Regular rate of 2:1
    public CustomerEntity getNextCustomer21() {
        return null;
    }

    @Transactional
    public void increaseCounter(CustomerType customerType) {
        if (customerType == CustomerType.VIP) {
            counterRepository.increaseVIPCounter(COUNTER_ID);
        } else {
            counterRepository.increaseRegularCounter(COUNTER_ID);
        }
    }
}
