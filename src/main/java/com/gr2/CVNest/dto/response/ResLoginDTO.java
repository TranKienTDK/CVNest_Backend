package com.gr2.CVNest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gr2.CVNest.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
        private Role role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String name;
    }
}
