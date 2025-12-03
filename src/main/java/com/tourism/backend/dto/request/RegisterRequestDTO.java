package com.tourism.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 3, max = 100, message = "Họ và tên phải từ 3-100 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Họ và tên chỉ được chứa chữ cái")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường và 1 số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirmPassword;

    @NotBlank(message = "Vui lòng chọn Tỉnh/Thành")
    private String provinceCode;

    private String provinceName;  // Frontend gửi lên

    @NotBlank(message = "Vui lòng chọn Quận/Huyện")
    private String districtCode;

    private String districtName;

    @AssertTrue(message = "Mật khẩu không khớp")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
