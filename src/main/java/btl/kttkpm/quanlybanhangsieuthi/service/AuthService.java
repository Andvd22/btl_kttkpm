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
}
