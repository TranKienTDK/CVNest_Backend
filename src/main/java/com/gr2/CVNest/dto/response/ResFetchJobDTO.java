package com.gr2.CVNest.dto.response;

import com.gr2.CVNest.util.constant.LevelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResFetchJobDTO {
    private long id;
    private String name;
    private String location;
    private int quantity;
    private LevelEnum level;
    private long startSalary;
    private long endSalary;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    private List<String> skills;
}
