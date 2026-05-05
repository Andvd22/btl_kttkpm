package btl.kttkpm.quanlybanhangsieuthi.service;

import btl.kttkpm.quanlybanhangsieuthi.dto.CustomerForm;
import btl.kttkpm.quanlybanhangsieuthi.entity.Customer;
import btl.kttkpm.quanlybanhangsieuthi.repository.CustomerRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return customerRepository.findAll();
        }
        return customerRepository.findByFullNameContainingIgnoreCaseOrPhoneNumberContaining(keyword.trim(), keyword.trim());
    }

    public Page<Customer> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        if (keyword == null || keyword.isBlank()) {
            return customerRepository.findAll(pageable);
        }
        String value = keyword.trim();
        return customerRepository.findByFullNameContainingIgnoreCaseOrPhoneNumberContaining(value, value, pageable);
    }

    public Customer getById(Integer id) {
        return customerRepository.findById(id).orElseThrow();
    }

    public Customer create(CustomerForm form) {
        if (customerRepository.existsByPhoneNumber(form.getPhoneNumber())) {
            throw new IllegalArgumentException("So dien thoai da ton tai");
        }
        Customer customer = new Customer();
        apply(customer, form);
        return customerRepository.save(customer);
    }

    public Customer update(Integer id, CustomerForm form) {
        Customer customer = getById(id);
        if (customerRepository.existsByPhoneNumberAndIdNot(form.getPhoneNumber(), id)) {
            throw new IllegalArgumentException("So dien thoai da ton tai");
        }
        apply(customer, form);
        return customerRepository.save(customer);
    }

    public CustomerForm toForm(Customer customer) {
        CustomerForm form = new CustomerForm();
        form.setId(customer.getId());
        form.setFullName(customer.getFullName());
        form.setAddress(customer.getAddress());
        form.setPhoneNumber(customer.getPhoneNumber());
        form.setEmail(customer.getEmail());
        form.setNote(customer.getNote());
        form.setTotalRevenue(customer.getTotalRevenue());
        form.setRewardPoint(customer.getRewardPoint());
        form.setStatus(customer.getStatus());
        return form;
    }

    private void apply(Customer customer, CustomerForm form) {
        customer.setFullName(form.getFullName());
        customer.setAddress(form.getAddress());
        customer.setPhoneNumber(form.getPhoneNumber());
        customer.setEmail(form.getEmail());
        customer.setNote(form.getNote());
        customer.setTotalRevenue(form.getTotalRevenue() == null ? 0f : form.getTotalRevenue());
        customer.setRewardPoint(form.getRewardPoint() == null ? 0f : form.getRewardPoint());
        customer.setStatus(form.getStatus() == null ? 1 : form.getStatus());
    }
}
