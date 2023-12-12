package com.cat.customerservice.repository;


import com.cat.customerservice.entity.CustomerServiceCounterEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerServiceCounterRepository extends CrudRepository<CustomerServiceCounterEntity, Integer> {

    @Modifying
    @Transactional
    @Query("update CustomerServiceCounterEntity cs set cs.vipServedCount = cs.vipServedCount + 1 where cs.id = ?1")
    int increaseVIPCounter(int id);

    @Modifying
    @Transactional
    @Query("update CustomerServiceCounterEntity cs set cs.regularServedCount = cs.regularServedCount + 1 where cs.id = ?1")
    int increaseRegularCounter(int id);
}
