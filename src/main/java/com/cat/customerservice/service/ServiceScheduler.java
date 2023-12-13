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
        Optional<CustomerEntity> vipOpt = customerRepository.findNextCheckinCustomerByType(CustomerType.VIP);
        if (vipOpt.isPresent()) {
            CustomerEntity customerEntity = vipOpt.get();
            customerEntity.setServingStatus(ServingStatus.SERVED);
            customerRepository.save(customerEntity);
            increaseCounter(CustomerType.VIP);
            return Optional.of(customerMapper.entityToApi(vipOpt.get()));
        }

        // try to serve a regular customer since no more vips
        Optional<CustomerEntity> regularOpt = customerRepository.findNextCheckinCustomerByType(CustomerType.REGULAR);
        if (regularOpt.isPresent()) {
            CustomerEntity customerEntity = regularOpt.get();
            customerEntity.setServingStatus(ServingStatus.SERVED);
            customerRepository.save(customerEntity);
            increaseCounter(CustomerType.REGULAR);
            return Optional.of(customerMapper.entityToApi(regularOpt.get()));
        }

        return Optional.empty();
    }


    /**
     * Try to serve the next customer with the ratio of 2:1 of (VIPs : Regulars).
     * If there is no more next customer in the desired type, then it will try to serve
     * a customer from other type.
     */
    @Transactional
    public Optional<Customer> getNextCustomer21() {
        CustomerServiceCounterEntity counter = counterRepository.findById(COUNTER_ID).get();
        CustomerType nextCustomerType
                = getNextCustomerType(counter.getVipServedCount(), counter.getRegularServedCount());

        LOG.debug("try to get next customer for CustomerType {}", nextCustomerType);
        Optional<CustomerEntity> nextCustomerOpt
                = customerRepository.findNextCheckinCustomerByType(nextCustomerType);
        if (nextCustomerOpt.isPresent()) {
            LOG.debug("found next customer: {}", nextCustomerOpt.get());
            CustomerEntity nextCustomer = nextCustomerOpt.get();
            nextCustomer.setServingStatus(ServingStatus.SERVED);
            customerRepository.save(nextCustomer);
            return Optional.of(customerMapper.entityToApi(nextCustomer));
        }

        LOG.debug("can not find next customer for the desired CustomerType {}", nextCustomerType);

        CustomerType otherCustomerType = nextCustomerType == CustomerType.VIP
                ? CustomerType.REGULAR
                : CustomerType.VIP;

        LOG.debug("try to find next customer for the other CustomerType {}", otherCustomerType);

        nextCustomerOpt
                = customerRepository.findNextCheckinCustomerByType(otherCustomerType);
        if (nextCustomerOpt.isPresent()) {
            LOG.debug("found next customer: {}", nextCustomerOpt.get());
            CustomerEntity nextCustomer = nextCustomerOpt.get();
            nextCustomer.setServingStatus(ServingStatus.SERVED);
            customerRepository.save(nextCustomer);
            return Optional.of(customerMapper.entityToApi(nextCustomer));
        }

        LOG.debug("no more customer to serve");
        return Optional.empty();
    }

    @Transactional
    public void increaseCounter(CustomerType customerType) {
        if (customerType == CustomerType.VIP) {
            counterRepository.increaseVIPCounter(COUNTER_ID);
        } else {
            counterRepository.increaseRegularCounter(COUNTER_ID);
        }
    }

    /**
     *  Try to serve the (VIPs : Regulars) with a (2 : 1) ratio
     */
    CustomerType getNextCustomerType(int vipServedCount, int regularServedCount) {
        LOG.debug("getNextCustomerType, vipServedCount: {}, regularServedCount: {}",
                vipServedCount, regularServedCount);

        if (regularServedCount == 0) {
            if (vipServedCount < 2) {
                return CustomerType.VIP;
            } else {
                return CustomerType.REGULAR;
            }
        }

        double ratio = vipServedCount * 1.0 / regularServedCount;
        LOG.debug("getNextCustomerType, vip : regular, ratio: {}", ratio);
        if (ratio <= 2.0) {
            return CustomerType.VIP;
        } else {
            return CustomerType.REGULAR;
        }
    }
}
