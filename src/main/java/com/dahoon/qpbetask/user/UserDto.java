package com.dahoon.qpbetask.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotBlank
    private String password;

    public static UserDto toDto(User user) {
        return new UserDto(user.getUsername(), user.getPassword());
    }
}
