package btl.kttkpm.quanlybanhangsieuthi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_manager")
@PrimaryKeyJoinColumn(name = "tbl_staff_tbl_user_id")
public class Manager extends Staff {

    @Column(nullable = false, unique = true, length = 255)
    private String managerCode;

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }
}
