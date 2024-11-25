package com.moliveira.demo_park_api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPasswordDto {

    @NotBlank
    @Size(min = 6, max = 6)
    private String actualPassword;
    @NotBlank
    @Size(min = 6, max = 6)
    private String newPassword;
    @NotBlank
    @Size(min = 6, max = 6)
    private String confirmedPassword;
}
