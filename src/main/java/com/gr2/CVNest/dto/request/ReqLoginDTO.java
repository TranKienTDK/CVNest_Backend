package com.gr2.CVNest.dto.request;

import com.gr2.CVNest.util.annotation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "Email cannot blank")
    @ValidEmail
    private String email;

    @NotBlank(message = "Password cannot blank")
    private String password;
}
