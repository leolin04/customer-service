package com.cat.customerservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "customer_service_counters")
public class CustomerServiceCounterEntity {
    @Id
    private int id;

    private int vipServedCount;

    private int regularServedCount;

    @Version
    private Integer version;

    public CustomerServiceCounterEntity(){
    }

    public CustomerServiceCounterEntity(int id, int vipServedCount, int regularServedCount) {
        this.id = id;
        this.vipServedCount = vipServedCount;
        this.regularServedCount = regularServedCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVipServedCount() {
        return vipServedCount;
    }

    public void setVipServedCount(int vipServedCount) {
        this.vipServedCount = vipServedCount;
    }

    public int getRegularServedCount() {
        return regularServedCount;
    }

    public void setRegularServedCount(int regularServedCount) {
        this.regularServedCount = regularServedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerServiceCounterEntity that = (CustomerServiceCounterEntity) o;

        if (id != that.id) return false;
        if (vipServedCount != that.vipServedCount) return false;
        return regularServedCount == that.regularServedCount;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + vipServedCount;
        result = 31 * result + regularServedCount;
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CustomerServiceCounter{");
        sb.append("id=").append(id);
        sb.append(", vipServedCount=").append(vipServedCount);
        sb.append(", regularServedCount=").append(regularServedCount);
        sb.append('}');
        return sb.toString();
    }
}
