package btl.kttkpm.quanlybanhangsieuthi.config;

import btl.kttkpm.quanlybanhangsieuthi.entity.Customer;
import btl.kttkpm.quanlybanhangsieuthi.entity.Item;
import btl.kttkpm.quanlybanhangsieuthi.entity.Manager;
import btl.kttkpm.quanlybanhangsieuthi.entity.Staff;
import btl.kttkpm.quanlybanhangsieuthi.repository.CustomerRepository;
import btl.kttkpm.quanlybanhangsieuthi.repository.ItemRepository;
import btl.kttkpm.quanlybanhangsieuthi.repository.ManagerRepository;
import btl.kttkpm.quanlybanhangsieuthi.repository.StaffRepository;
import btl.kttkpm.quanlybanhangsieuthi.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            StaffRepository staffRepository,
            ManagerRepository managerRepository,
            CustomerRepository customerRepository,
            ItemRepository itemRepository) {
        return args -> {
            if (userRepository.findByUsername("dovanan").isEmpty()) {
                Manager manager = new Manager();
                manager.setFullName("Đỗ Văn An");
                manager.setPhoneNumber("0900000001");
                manager.setUsername("dovanan");
                manager.setPassword("123@n");
                manager.setDateOfBirth(LocalDate.of(2004, 1, 1));
                manager.setAddress("Hà Nội");
                manager.setEmail("dovanan@gmail.com");
                manager.setStatus(1);
                manager.setSalary(15000000f);
                manager.setPosition("Quản lý");
                manager.setManagerCode("QL001");
                managerRepository.save(manager);
            }

            boolean cashierExists = staffRepository.findAll().stream()
                    .anyMatch(staff -> "Nhân viên thu ngân".equals(staff.getPosition()));
            if (!cashierExists) {
                Staff cashier = new Staff();
                cashier.setFullName("Đỗ Văn An Thu Ngân");
                cashier.setPhoneNumber("0900000002");
                cashier.setUsername("thu.ngan");
                cashier.setPassword("123@n");
                cashier.setDateOfBirth(LocalDate.of(2003, 5, 10));
                cashier.setAddress("Hà Nội");
                cashier.setEmail("thungan@qlbhst.vn");
                cashier.setStatus(1);
                cashier.setSalary(9000000f);
                cashier.setPosition("Nhân viên thu ngân");
                staffRepository.save(cashier);
            }

            if (customerRepository.count() == 0) {
                customerRepository.save(createCustomer(
                        "Đỗ Văn An KH 1",
                        "0911111111",
                        "kh1.dovanan@gmail.com",
                        "Hà Nội",
                        "Khách hàng thân thiết",
                        3500000f,
                        12.5f,
                        1));
                customerRepository.save(createCustomer(
                        "Đỗ Văn An KH 2",
                        "0922222222",
                        "kh2.dovanan@gmail.com",
                        "Nam Định",
                        "Khách hàng mới",
                        0f,
                        0f,
                        1));
            }
            for (int i = (int) customerRepository.count() + 1; i <= 80; i++) {
                customerRepository.save(createCustomer(
                        "Đỗ Văn An KH " + i,
                        String.format("09%08d", i),
                        "kh" + i + ".dovanan@gmail.com",
                        i % 2 == 0 ? "Hà Nội" : "Nam Định",
                        i % 3 == 0 ? "Khách hàng tích điểm" : "Khách hàng thường",
                        i * 125000f,
                        (float) (i % 10),
                        1));
            }

            if (itemRepository.count() == 0) {
                itemRepository.save(createItem("Sữa tươi Vinamilk", "Sữa", "Hộp", 32000f, 120, "Sữa tươi có đường", 1));
                itemRepository.save(createItem("Mì Hảo Hảo", "Mì ăn liền", "Gói", 4500f, 500, "Mì tôm chua cay", 1));
                itemRepository.save(createItem("Nước suối Aquafina", "Nước uống", "Chai", 7000f, 250, "Nước suối đóng chai", 1));
                itemRepository.save(createItem("Bánh Oreo", "Bánh kẹo", "Gói", 15000f, 180, "Bánh quy socola", 1));
            }
            String[] categories = {"Đồ uống", "Bánh kẹo", "Gia vị", "Đồ khô", "Sữa"};
            String[] units = {"Chai", "Gói", "Hộp", "Lon", "Túi"};
            for (int i = (int) itemRepository.count() + 1; i <= 50; i++) {
                itemRepository.save(createItem(
                        "Mặt hàng Đỗ Văn An " + i,
                        categories[i % categories.length],
                        units[i % units.length],
                        5000f + (i * 1750f),
                        40 + (i * 3),
                        "Dữ liệu test mặt hàng số " + i,
                        1));
            }
        };
    }

    private Customer createCustomer(
            String fullName,
            String phoneNumber,
            String email,
            String address,
            String note,
            Float totalRevenue,
            Float rewardPoint,
            Integer status) {
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setNote(note);
        customer.setTotalRevenue(totalRevenue);
        customer.setRewardPoint(rewardPoint);
        customer.setStatus(status);
        return customer;
    }

    private Item createItem(
            String name,
            String category,
            String unit,
            Float salePrice,
            Integer stockQuantity,
            String description,
            Integer status) {
        Item item = new Item();
        item.setName(name);
        item.setCategory(category);
        item.setUnit(unit);
        item.setSalePrice(salePrice);
        item.setStockQuantity(stockQuantity);
        item.setDescription(description);
        item.setStatus(status);
        return item;
    }
}
