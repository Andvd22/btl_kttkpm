package btl.kttkpm.quanlybanhangsieuthi.repository;

import btl.kttkpm.quanlybanhangsieuthi.entity.Customer;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByFullNameContainingIgnoreCaseOrPhoneNumberContaining(String fullName, String phoneNumber);

    Page<Customer> findByFullNameContainingIgnoreCaseOrPhoneNumberContaining(String fullName, String phoneNumber, Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);
}
