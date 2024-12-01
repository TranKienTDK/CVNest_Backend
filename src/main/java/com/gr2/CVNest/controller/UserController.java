package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.request.ReqUpdateUserDTO;
import com.gr2.CVNest.dto.response.ResUpdateUserDTO;
import com.gr2.CVNest.dto.response.ResUploadAvatarDTO;
import com.gr2.CVNest.entity.User;
import com.gr2.CVNest.service.UserService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/users/{userId}/upload-avatar")
    @ApiMessage("Set avatar successful")
    public ResponseEntity<ResUploadAvatarDTO> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @PathVariable("userId") Long userId) {
        try {
            String imageUrl = userService.uploadAvatar(file, userId);
            ResUploadAvatarDTO res = new ResUploadAvatarDTO();
            res.setAvatar(imageUrl);
            return ResponseEntity.ok(res);
        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
