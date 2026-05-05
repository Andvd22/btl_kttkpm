package btl.kttkpm.quanlybanhangsieuthi.service;

import btl.kttkpm.quanlybanhangsieuthi.dto.BillCartItem;
import btl.kttkpm.quanlybanhangsieuthi.dto.CartItemForm;
import btl.kttkpm.quanlybanhangsieuthi.dto.CheckoutForm;
import btl.kttkpm.quanlybanhangsieuthi.entity.Bill;
import btl.kttkpm.quanlybanhangsieuthi.entity.BillDetail;
import btl.kttkpm.quanlybanhangsieuthi.entity.Customer;
import btl.kttkpm.quanlybanhangsieuthi.entity.Item;
import btl.kttkpm.quanlybanhangsieuthi.entity.Staff;
import btl.kttkpm.quanlybanhangsieuthi.repository.BillRepository;
import btl.kttkpm.quanlybanhangsieuthi.repository.CustomerRepository;
import btl.kttkpm.quanlybanhangsieuthi.repository.ItemRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillService {

    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;

    public BillService(
            ItemRepository itemRepository,
            CustomerRepository customerRepository,
            BillRepository billRepository) {
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.billRepository = billRepository;
    }

    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return itemRepository.findAll();
        }
        return itemRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

    public Page<Item> searchItems(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        if (keyword == null || keyword.isBlank()) {
            return itemRepository.findAll(pageable);
        }
        return itemRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return customerRepository.findAll();
        }
        return customerRepository.findByFullNameContainingIgnoreCaseOrPhoneNumberContaining(keyword.trim(), keyword.trim());
    }

    public Page<Customer> searchCustomers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        if (keyword == null || keyword.isBlank()) {
            return customerRepository.findAll(pageable);
        }
        String value = keyword.trim();
        return customerRepository.findByFullNameContainingIgnoreCaseOrPhoneNumberContaining(value, value, pageable);
    }

    public Customer findCustomerById(Integer id) {
        if (id == null) {
            return null;
        }
        return customerRepository.findById(id).orElse(null);
    }

    public BillCartItem createCartItem(CartItemForm form) {
        Item item = itemRepository.findById(form.getItemId()).orElseThrow();
        if (item.getStatus() != 1) {
            throw new IllegalArgumentException("Mat hang dang ngung kinh doanh");
        }
        if (form.getQuantity() > item.getStockQuantity()) {
            throw new IllegalArgumentException("So luong vuot qua ton kho");
        }
        BillCartItem cartItem = new BillCartItem();
        cartItem.setItemId(item.getId());
        cartItem.setItemName(item.getName());
        cartItem.setQuantity(form.getQuantity());
        cartItem.setUnitPrice(item.getSalePrice());
        cartItem.setLineAmount(item.getSalePrice() * form.getQuantity());
        return cartItem;
    }

    @Transactional
    public Bill checkout(Staff staff, CheckoutForm form, List<BillCartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Hoa don chua co mat hang");
        }

        float totalAmount = 0f;
        int totalQuantity = 0;
        for (BillCartItem cartItem : cartItems) {
            Item item = itemRepository.findById(cartItem.getItemId()).orElseThrow();
            if (cartItem.getQuantity() > item.getStockQuantity()) {
                throw new IllegalArgumentException("Mat hang " + item.getName() + " khong du so luong ton kho");
            }
            totalAmount += cartItem.getLineAmount();
            totalQuantity += cartItem.getQuantity();
        }

        String paymentMethod = form.getPaymentMethod() == null ? "" : form.getPaymentMethod().trim().toUpperCase();
        Customer customer = null;
        if (form.getCustomerId() != null) {
            customer = customerRepository.findById(form.getCustomerId()).orElse(null);
        }

        float usedPoint = 0f;
        float discountAmount = 0f;
        if (customer != null && Boolean.TRUE.equals(form.getUseRewardPoint())) {
            int availablePoint = (int) Math.floor(customer.getRewardPoint() == null ? 0f : customer.getRewardPoint());
            int maxUsablePointByBill = (int) Math.floor(totalAmount / 1000f);
            usedPoint = Math.min(availablePoint, maxUsablePointByBill);
            discountAmount = usedPoint * 1000f;
        }
        float paidAmount = totalAmount - discountAmount;
        float earnedPoint = paidAmount / 100000f;

        Bill bill = new Bill();
        bill.setCreatedTime(LocalDate.now());
        bill.setPaymentTime(LocalDate.now());
        bill.setPaymentMethod(paymentMethod);
        bill.setStatus(1);
        bill.setNote(form.getNote());
        bill.setStaff(staff);
        bill.setTotalAmount(totalAmount);
        bill.setTotalQuantity(totalQuantity);
        bill.setPaidAmount(paidAmount);
        bill.setDiscountAmount(discountAmount);
        bill.setUsedPoint(usedPoint);
        bill.setEarnedPoint(earnedPoint);
        bill.setCustomer(customer);

        if (paidAmount <= 0f) {
            bill.setReceivedAmount(0f);
            bill.setChangeAmount(0f);
        } else if ("CASH".equals(paymentMethod)) {
            if (form.getReceivedAmount() == null) {
                throw new IllegalArgumentException("Vui long nhap so tien khach dua");
            }
            if (form.getReceivedAmount() < paidAmount) {
                throw new IllegalArgumentException("So tien khach dua khong du");
            }
            bill.setReceivedAmount(form.getReceivedAmount());
            bill.setChangeAmount(form.getReceivedAmount() - paidAmount);
        } else if ("QR".equals(paymentMethod) || "CARD".equals(paymentMethod)) {
            if (!Boolean.TRUE.equals(form.getPaymentConfirmed())) {
                throw new IllegalArgumentException("Giao dich thanh toan chua thanh cong");
            }
            bill.setReceivedAmount(0f);
            bill.setChangeAmount(0f);
        } else {
            throw new IllegalArgumentException("Phuong thuc thanh toan khong hop le");
        }

        List<BillDetail> details = new ArrayList<>();
        for (BillCartItem cartItem : cartItems) {
            Item item = itemRepository.findById(cartItem.getItemId()).orElseThrow();
            item.setStockQuantity(item.getStockQuantity() - cartItem.getQuantity());
            itemRepository.save(item);

            BillDetail detail = new BillDetail();
            detail.setBill(bill);
            detail.setItem(item);
            detail.setQuantity(cartItem.getQuantity());
            detail.setUnitPrice(cartItem.getUnitPrice());
            detail.setLineAmount(cartItem.getLineAmount());
            details.add(detail);
        }

        bill.setBillDetails(details);
        Bill savedBill = billRepository.save(bill);

        if (savedBill.getCustomer() != null) {
            customer = savedBill.getCustomer();
            customer.setTotalRevenue(customer.getTotalRevenue() + paidAmount);
            float currentPoint = customer.getRewardPoint() == null ? 0f : customer.getRewardPoint();
            customer.setRewardPoint(currentPoint - usedPoint + earnedPoint);
            customerRepository.save(customer);
        }

        return savedBill;
    }
}
