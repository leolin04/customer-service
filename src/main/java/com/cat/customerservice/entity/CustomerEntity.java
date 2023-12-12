package com.cat.customerservice.entity;


import org.springframework.data.annotation.Version;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "customers",
        indexes = {
        @Index(
                name = "customers_type_checkin",
                columnList = "customerType,checkInTime"
        )})
public class CustomerEntity {
    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    private Date checkInTime;

    @Enumerated(EnumType.STRING)
    private ServingStatus servingStatus;

    @Version
    private Integer version;

    public CustomerEntity() {
    }

    public CustomerEntity(String name, String phoneNumber, CustomerType customerType, Date checkInTime, ServingStatus servingStatus) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.customerType = customerType;
        this.checkInTime = checkInTime;
        this.servingStatus = servingStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public ServingStatus getServingStatus() {
        return servingStatus;
    }

    public void setServingStatus(ServingStatus servingStatus) {
        this.servingStatus = servingStatus;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerEntity customerEntity = (CustomerEntity) o;

        if (id != customerEntity.id) return false;
        if (!Objects.equals(name, customerEntity.name)) return false;
        if (!Objects.equals(phoneNumber, customerEntity.phoneNumber))
            return false;
        if (customerType != customerEntity.customerType) return false;
        if (!Objects.equals(checkInTime, customerEntity.checkInTime))
            return false;
        if (servingStatus != customerEntity.servingStatus) return false;
        return Objects.equals(version, customerEntity.version);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (customerType != null ? customerType.hashCode() : 0);
        result = 31 * result + (checkInTime != null ? checkInTime.hashCode() : 0);
        result = 31 * result + (servingStatus != null ? servingStatus.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Customer{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", customerType=").append(customerType);
        sb.append(", checkInTime=").append(checkInTime);
        sb.append(", servingStatus=").append(servingStatus);
        sb.append(", version=").append(version);
        sb.append('}');
        return sb.toString();
    }
}
