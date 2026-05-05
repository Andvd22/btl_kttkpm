package btl.kttkpm.quanlybanhangsieuthi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_staff")
@PrimaryKeyJoinColumn(name = "tbl_user_id")
public class Staff extends User {

    @Column(nullable = false)
    private Float salary;

    @Column(nullable = false, length = 255)
    private String position;

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
