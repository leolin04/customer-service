package com.cat.customerservice.repository;


import com.cat.customerservice.entity.CustomerEntity;
import com.cat.customerservice.entity.CustomerType;
import com.cat.customerservice.entity.ServingStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Integer> {
    Optional<CustomerEntity> findTopByCustomerTypeAndServingStatusOrderByCheckInTimeAsc(
            CustomerType customerType,
            ServingStatus servingStatus);

}
