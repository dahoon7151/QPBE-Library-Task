package com.dahoon.qpbetask.user.dto;

import com.dahoon.qpbetask.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
            message = "비밀번호 형식이 올바르지 않습니다. 8자 이상, 숫자 및 특수문자(@$!%*?&#) 포함, 영어,숫자,특수문자만 가능")
    private String password;

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword());
    }
}
