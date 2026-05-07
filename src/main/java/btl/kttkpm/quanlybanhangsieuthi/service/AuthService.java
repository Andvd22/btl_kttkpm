package btl.kttkpm.quanlybanhangsieuthi.service;

import btl.kttkpm.quanlybanhangsieuthi.entity.User;
import btl.kttkpm.quanlybanhangsieuthi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password) && user.getStatus() == 1)
                .orElse(null);
    }

    public User register(String fullName, String phoneNumber, String username, String password, String confirmPassword,
            String address, String email) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xac nhan password khong khop");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username da ton tai");
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("So dien thoai da ton tai");
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setPhoneNumber(phoneNumber.trim());
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setAddress(address == null ? null : address.trim());
        user.setEmail(email == null ? null : email.trim());
        user.setStatus(1);
        return userRepository.save(user);
    }
}
