package com.cat.customerservice.service;


import com.cat.customerservice.api.Customer;
import com.cat.customerservice.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mappings({
            @Mapping(target = "ticketId", source = "id")
    })
    Customer entityToApi(CustomerEntity customerEntity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "checkInTime", ignore = true),
            @Mapping(target = "ticketId", ignore = true),
            @Mapping(target = "servingStatus", ignore = true),
    })
    CustomerEntity apiToEntity(Customer customer);
}
