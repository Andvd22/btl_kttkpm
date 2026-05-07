package btl.kttkpm.quanlybanhangsieuthi.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Ho ten khong duoc de trong")
    private String fullName;

    @NotBlank(message = "So dien thoai khong duoc de trong")
    private String phoneNumber;

    @NotBlank(message = "Username khong duoc de trong")
    private String username;

    @NotBlank(message = "Password khong duoc de trong")
    private String password;

    @NotBlank(message = "Xac nhan password khong duoc de trong")
    private String confirmPassword;

    private String address;

    private String email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
