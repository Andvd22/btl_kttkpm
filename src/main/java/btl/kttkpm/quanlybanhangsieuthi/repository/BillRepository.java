package btl.kttkpm.quanlybanhangsieuthi.repository;

import btl.kttkpm.quanlybanhangsieuthi.entity.Bill;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Integer> {

    List<Bill> findByPaymentTimeBetweenAndStatus(LocalDate startDate, LocalDate endDate, Integer status);
}
