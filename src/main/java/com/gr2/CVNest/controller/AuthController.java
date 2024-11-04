package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.request.ReqRegisterDTO;
import com.gr2.CVNest.dto.request.ReqResendEmailDTO;
import com.gr2.CVNest.entity.User;
import com.gr2.CVNest.service.UserService;
import com.gr2.CVNest.util.SecurityUtil;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.EmailAlreadyExistException;
import com.gr2.CVNest.util.error.EmailNotFoundException;
import com.gr2.CVNest.util.error.VerifyCodeInvalidException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;


    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                          UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    // SIGN UP
    @PostMapping("/auth/register")
    @ApiMessage("Register success")
    public ResponseEntity<Void> registerNewUser(@Valid @RequestBody ReqRegisterDTO reqRegisterDTO) throws EmailAlreadyExistException, MessagingException {
        if (this.userService.checkEmailExists(reqRegisterDTO.getEmail())) {
            throw new EmailAlreadyExistException("Email already exist");
        }

        this.userService.register(reqRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/auth/verify")
    @ApiMessage("Your account has been verified successfully")
    public ResponseEntity<Void> verifyUserByEmailCode(@RequestParam("code") String code,
                                                      @RequestParam("email") String email) throws VerifyCodeInvalidException {
        if (this.userService.verifyEmailCode(code, email)) {
            return ResponseEntity.ok().body(null);
        } else {
          throw new VerifyCodeInvalidException("Code is invalid or expired");
        }
    }

    @PostMapping("/auth/resend-verification")
    @ApiMessage("Email verify has been sent successfully")
    public ResponseEntity<Void> resendEmailVerification(@RequestBody ReqResendEmailDTO reqResendEmailDTO) throws EmailNotFoundException, MessagingException {
        User user = this.userService.handleGetUserByEmail(reqResendEmailDTO.getEmail());
        if (user == null) {
            throw new EmailNotFoundException("Email not found or exist");
        }
        this.userService.resendEmailVerification(user);
        return ResponseEntity.ok().body(null);
    }
}
