package com.gr2.CVNest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateCompanyDTO {
    @NotBlank(message = "Name cannot blank")
    private String name;
    private String description;
    private String address;
    private String logo;
}
