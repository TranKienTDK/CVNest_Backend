package com.gr2.CVNest.dto.request;

import com.gr2.CVNest.util.constant.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateUserDTO {
    @NotNull(message = "ID cannot blank")
    private long id;
    private String fullName;
    private GenderEnum gender;
    private int age;
    private String phone;
    private String address;
}
