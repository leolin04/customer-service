package com.cat.customerservice.controller;


import com.cat.customerservice.MySqlTestBase;
import com.cat.customerservice.api.Customer;
import com.cat.customerservice.entity.CustomerType;
import com.cat.customerservice.service.ServiceScheduler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CustomerServiceControllerUnitTests extends MySqlTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceControllerUnitTests.class);

    @Autowired
    private WebTestClient client;

    @MockBean
    private ServiceScheduler scheduler;

    @Test
    public void testCheckin() {
        Customer customer = new Customer(1, "full name", "phone number", CustomerType.REGULAR);
        when(scheduler.checkIn(any())).thenReturn(customer);

        WebTestClient.BodyContentSpec responseSpec =
            client
                .post()
                .uri("/v1/checkin")
                .body(BodyInserters.fromValue(customer))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();

            responseSpec
                    .jsonPath("$.ticketId").isEqualTo(customer.getTicketId())
                    .jsonPath("$.name").isEqualTo(customer.getName())
                    .jsonPath("$.phoneNumber").isEqualTo(customer.getPhoneNumber())
                    .jsonPath("$.customerType").isEqualTo(customer.getCustomerType().name());
    }

    @Test
    public void testGetNextCustomer() {
        // when there is a customer
        {
          Customer customer = new Customer(1, "full name", "phone number", CustomerType.REGULAR);
          when(scheduler.getNextCustomer()).thenReturn(Optional.of(customer));

          WebTestClient.BodyContentSpec responseSpec =
              client
                  .get()
                  .uri("/v1/nextcustomer")
                  .exchange()
                  .expectStatus().isEqualTo(HttpStatus.OK)
                  .expectHeader().contentType(MediaType.APPLICATION_JSON)
                  .expectBody();

          responseSpec
              .jsonPath("$.ticketId").isEqualTo(customer.getTicketId())
              .jsonPath("$.name").isEqualTo(customer.getName())
              .jsonPath("$.phoneNumber").isEqualTo(customer.getPhoneNumber())
              .jsonPath("$.customerType").isEqualTo(customer.getCustomerType().name());
        }

        // when there is no more customers
        {
            when(scheduler.getNextCustomer()).thenReturn(Optional.empty());
            WebTestClient.BodyContentSpec responseSpec =
                client
                    .get()
                    .uri("/v1/nextcustomer")
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.OK)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody();

            responseSpec
                    .jsonPath("$.ticketId")
                    .isEqualTo(-1);
        }
    }

    @Test
    public void testGetNextCustomer21() {
        // when there is a customer
        {
            Customer customer = new Customer(1, "full name", "phone number", CustomerType.REGULAR);
            when(scheduler.getNextCustomer21()).thenReturn(Optional.of(customer));

            WebTestClient.BodyContentSpec responseSpec =
                client
                    .get()
                    .uri("/v1/nextcustomer21")
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.OK)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody();

            responseSpec
                    .jsonPath("$.ticketId").isEqualTo(customer.getTicketId())
                    .jsonPath("$.name").isEqualTo(customer.getName())
                    .jsonPath("$.phoneNumber").isEqualTo(customer.getPhoneNumber())
                    .jsonPath("$.customerType").isEqualTo(customer.getCustomerType().name());
        }

        // when there is no more customers
        {
            when(scheduler.getNextCustomer21()).thenReturn(Optional.empty());
            WebTestClient.BodyContentSpec responseSpec =
                client
                    .get()
                    .uri("/v1/nextcustomer21")
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.OK)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody();

            responseSpec
                    .jsonPath("$.ticketId")
                    .isEqualTo(-1);
        }
    }



}
