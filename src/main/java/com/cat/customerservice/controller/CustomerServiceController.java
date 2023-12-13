package com.cat.customerservice.controller;


import com.cat.customerservice.api.Customer;
import com.cat.customerservice.exception.NoMoreContentException;
import com.cat.customerservice.service.ServiceScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Next Customer. " +
                            "If there is no more customers, then a dummy customer with {ticketId:-1} is returned. " +
                            "Returning the dummy customer might be more api client friendly than HTTP 204")})
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
                Customer empty = new Customer();
                empty.setTicketId(-1);
                return empty;
            }
        } catch (RuntimeException ex) {
            LOG.warn("getNextCustomer failed", ex);
            throw ex;
        }
    }

    @Operation(
            summary = "Get next customer in 2:1 ratio of (VIP : Regular)",
            description = "Try to get next customer in 2:1 ratio of (VIP : Regular). " +
                    "If there is no more customer with desired customer type, then it will try to " +
                    "get next customer from the other type.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Next Customer. " +
                            "If there is no more customers, then a dummy customer with {ticketId:-1} is returned. " +
                            "Returning the dummy customer might be more api client friendly than HTTP 204")})
    @GetMapping(
            value = "/v1/nextcustomer21",
            produces = "application/json"
    )
    public Customer getNextCustomer21() {
        try {
            LOG.debug("trying to get the next customer in 2:1 ratio of VIP : Regular");
            Optional<Customer> customerOpt = serviceScheduler.getNextCustomer21();
            if (customerOpt.isPresent()) {
                return customerOpt.get();
            } else {
                Customer empty = new Customer();
                empty.setTicketId(-1);
                return empty;
            }
        } catch (RuntimeException ex) {
            LOG.warn("getNextCustomer21 failed", ex);
            throw ex;
        }
    }

}
