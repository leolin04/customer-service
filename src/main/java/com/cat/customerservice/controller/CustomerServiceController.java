package com.cat.customerservice.controller;


import com.cat.customerservice.api.Customer;
import com.cat.customerservice.exception.NotFoundException;
import com.cat.customerservice.service.ServiceScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CustomerServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceController.class);

    private final ServiceScheduler serviceScheduler;

    @Autowired
    public CustomerServiceController(ServiceScheduler serviceScheduler) {
        this.serviceScheduler = serviceScheduler;
    }

    @Operation(
            summary = "Check In Customer",
            description = "Check In Customer")
    @PostMapping(
            value = "/v1/checkin",
            consumes = "application/json",
            produces = "application/json"
    )
    public Customer checkIn(@RequestBody Customer customer) {
        try {
            LOG.debug("trying to checkin customer : {}", customer);
            return serviceScheduler.checkIn(customer);
        } catch (RuntimeException ex) {
            LOG.warn("Checkin failed", ex);
            throw ex;
        }
    }

    @Operation(
            summary = "Get Next Customer",
            description = "Will try to service the VIP customers first, then the Regular customers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Next Customer"),
            @ApiResponse(responseCode = "404", description =
                    "If there is no more customer to serve")
    })
    @GetMapping(
            value = "/v1/nextcustomer",
            produces = "application/json"
    )
    public Customer getNextCustomer() {
        try {
            LOG.debug("trying to get the next customer");
            Optional<Customer> customerOpt = serviceScheduler.getNextCustomer();
            if (customerOpt.isPresent()) {
                return customerOpt.get();
            } else {
                throw new NotFoundException("no more customers to serve");
            }
        } catch (RuntimeException ex) {
            LOG.warn("getNextCustomer failed", ex);
            throw ex;
        }
    }

}
