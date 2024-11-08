package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.request.ReqUpdateUserDTO;
import com.gr2.CVNest.dto.response.ResUpdateUserDTO;
import com.gr2.CVNest.entity.User;
import com.gr2.CVNest.service.UserService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users")
    @ApiMessage("Update user successful")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@Valid @RequestBody ReqUpdateUserDTO req) throws UserNotFoundException {
        User user = this.userService.handleGetUserById(req.getId());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        ResUpdateUserDTO res = this.userService.convertEntityToUpdateUserDTO(this.userService.updateUser(req));
        return ResponseEntity.ok().body(res);
    }

}
