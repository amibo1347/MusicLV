package com.musiclv.member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자를 넘을 수 없습니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
             message = "비밀번호는 영문과 숫자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자를 넘을 수 없습니다.")
    private String name;

    @NotBlank(message = "연락처를 입력해주세요.")
    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
             message = "연락처 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phone;

    @NotBlank(message = "주소를 입력해주세요.")
    @Size(max = 255, message = "주소는 255자를 넘을 수 없습니다.")
    private String address;

    /** 이용약관 동의 */
    @AssertTrue(message = "이용약관에 동의해야 가입할 수 있습니다.")
    private boolean agreed;

    public boolean isPasswordMatched() {
        return password != null && password.equals(passwordConfirm);
    }
}
