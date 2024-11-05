package com.gr2.CVNest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPasswordDTO {
    @NotBlank(message = "Password cannot blank")
    private String newPassword;
}
