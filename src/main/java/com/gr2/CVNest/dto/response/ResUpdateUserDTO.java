package com.gr2.CVNest.dto.response;

import com.gr2.CVNest.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateUserDTO {
    private long id;
    private String email;
    private String fullName;
    private GenderEnum gender;
    private int age;
    private String phone;
    private String address;
    private Instant updatedAt;
}
