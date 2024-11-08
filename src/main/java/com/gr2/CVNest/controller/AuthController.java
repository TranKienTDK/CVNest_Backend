package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.request.*;
import com.gr2.CVNest.dto.response.ResLoginDTO;
import com.gr2.CVNest.entity.User;
import com.gr2.CVNest.service.UserService;
import com.gr2.CVNest.util.SecurityUtil;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.EmailAlreadyExistException;
import com.gr2.CVNest.util.error.EmailNotFoundException;
import com.gr2.CVNest.util.error.UserNotFoundException;
import com.gr2.CVNest.util.error.VerifyCodeInvalidException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${cvnest.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;


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

    // LOGIN
    @PostMapping("/auth/login")
    @ApiMessage("Login success")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                reqLoginDTO.getEmail(), reqLoginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByEmail(reqLoginDTO.getEmail());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin user = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
                                                                    currentUserDB.getFullName());
            resLoginDTO.setUser(user);
        }

        // Create access token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUser());
        resLoginDTO.setAccessToken(access_token);

        // Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(reqLoginDTO.getEmail(), resLoginDTO);

        // Update user
        this.userService.updateUserToken(refresh_token, reqLoginDTO.getEmail());

        // Set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    // GET ACCOUNT
    @GetMapping("/auth/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
                                SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = this.userService.handleGetUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getFullName());
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userGetAccount);
    }

    // REFRESH TOKEN
    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refresh_token) throws UserNotFoundException {
        // Check valid
        Jwt decodeToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodeToken.getSubject();

        // Check by refresh token and email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new UserNotFoundException("Refresh Token is invalid");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByEmail(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin user = new ResLoginDTO.UserLogin(currentUserDB.getId(),
                    currentUserDB.getEmail(), currentUserDB.getFullName());
            res.setUser(user);
        }

        // Create new access token
        String access_token = this.securityUtil.createAccessToken(email, res.getUser());
        res.setAccessToken(access_token);

        // Create new refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(new_refresh_token, email);

        // Set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    // FORGOT PASSWORD
    @GetMapping("/auth/forgot-password")
    @ApiMessage("Verify code to change password has been sent")
    public ResponseEntity<Void> sendEmailChangePassword(@Valid @RequestBody ReqForgotPasswordDTO req) throws EmailNotFoundException, MessagingException {
        User user = this.userService.handleGetUserByEmail(req.getEmail());
        if (user == null) {
            throw new EmailNotFoundException("Email not found or exist");
        }

        this.userService.verifyEmailChangePassword(user);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/auth/verify-code-password")
    @ApiMessage("Verify code success")
    public ResponseEntity<Void> verifyCodeChangePassword(@RequestParam("code") String code,
                                                         @RequestParam("email") String email) throws VerifyCodeInvalidException {
        if (this.userService.verifyChangePassword(code, email)) {
            return ResponseEntity.ok().body(null);
        }
        else {
            throw new VerifyCodeInvalidException("Code is invalid or expired");
        }
    }

    @PostMapping("/auth/reset-password")
    @ApiMessage("Password changed success")
    public ResponseEntity<Void> resetNewPassword(@Valid @RequestBody ReqPasswordDTO req,
                                              @RequestParam("email") String email) {
        User user = this.userService.handleGetUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("Email not found or exist");
        }

        this.userService.resetPassword(user, req.getNewPassword());
        return ResponseEntity.ok().body(null);
    }

    // LOGOUT
    @PostMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws EmailNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals(null)) {
            throw new EmailNotFoundException("Access Token is invalid");
        }

        User user = this.userService.handleGetUserByEmail(email);
        if (user != null) {
            user.setRefreshToken(null);
            this.userService.handleUpdateUser(user);
        }

        // Remove refresh token from cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }
}
