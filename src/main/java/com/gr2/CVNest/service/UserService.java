package com.gr2.CVNest.service;

import com.gr2.CVNest.dto.request.ReqRegisterDTO;
import com.gr2.CVNest.entity.User;
import com.gr2.CVNest.repository.RoleRepository;
import com.gr2.CVNest.repository.UserRepository;
import com.gr2.CVNest.util.constraint.Constraints;
import com.gr2.CVNest.util.helper.VerificationCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final RoleRepository roleRepository;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender, RedisTemplate<String, String> redisTemplate, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
        this.roleRepository = roleRepository;
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    // SIGN UP
    public void register(ReqRegisterDTO newUser) throws MessagingException {
        User user = new User();
        user.setEmail(newUser.getEmail());
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        user.setPassword(hashPassword);
        user.setFullName(newUser.getFullName());
        user.setRole(roleRepository.getReferenceById(Constraints.ROLE_USER_ID));
        user.setActivated(false);

        this.userRepository.save(user);
        sendEmailVerification(user);

    }

    public void sendEmailVerification(User user) throws MessagingException {
        String toAddress = user.getEmail();
        String subject = "Welcome to CVNest! Please verify your email";
        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f1eae0b9; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border-radius: 10px;'>"
                + "<h1 style='text-align: center; color: #AD7D59;'>Welcome to CVNest!</h1>"
                + "<p>Hi [[name]],</p>"
                + "<p>Wowwee! Thanks for registering an account with CVNest. You're the coolest person around (and we can't wait for you to get started).</p>"
                + "<p>Before you get started, we'll need to verify your email. Please copy the code below to verify your email address:</p>"
                + "<div style='text-align: center; margin: 20px 0;'>"
                + "<p style='font-weight: bold; font-size: 20px;'>[[code]]</p>"
                + "</div>"
                + "<p>The email verification link will expire after 5 minutes.</p>"
                + "<p>If you did not register an account with CVNest, please ignore this email.</p>"
                + "<p>Thank you,<br>The CVNest Team</p>"
                + "<div style='text-align: right;'>"
                + "<img src='cid:image_logo' alt='CVNest Logo' style='width: 100px; height: auto;'>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        // Customize the content with the user's display name and verification URL
        content = content.replace("[[name]]", user.getFullName());
        String code = VerificationCodeGenerator.generateVerificationCode();
        content = content.replace("[[code]]", code);

        helper.setText(content, true);

        try {
            Resource res = new FileSystemResource(new File("E:\\image\\CVNest_logo.jpg"));
            helper.addInline("image_logo", res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mailSender.send(message);

        // Store userId and verify code to verification
        String redisKey = "verify:email:" + user.getEmail();
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
    }

    public boolean verifyEmailCode(String code, String email) {
        String redisKey = "verify:email:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(redisKey);
            User user = this.userRepository.findByEmail(email);
            if (user != null) {
                user.setActivated(true);
                this.userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    public void resendEmailVerification(User user) throws MessagingException {
        String toAddress = user.getEmail();
        String subject = "Welcome to CVNest! Please verify your email";
        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f1eae0b9; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border-radius: 10px;'>"
                + "<h1 style='text-align: center; color: #AD7D59;'>Welcome to CVNest!</h1>"
                + "<p>Hi [[name]],</p>"
                + "<p>Wowwee! Thanks for registering an account with CVNest. You're the coolest person around (and we can't wait for you to get started).</p>"
                + "<p>Before you get started, we'll need to verify your email. Please copy the code below to verify your email address:</p>"
                + "<div style='text-align: center; margin: 20px 0;'>"
                + "<p style='font-weight: bold; font-size: 20px;'>[[code]]</p>"
                + "</div>"
                + "<p>The email verification link will expire after 5 minutes.</p>"
                + "<p>If you did not register an account with CVNest, please ignore this email.</p>"
                + "<p>Thank you,<br>The CVNest Team</p>"
                + "<div style='text-align: right;'>"
                + "<img src='cid:image_logo' alt='CVNest Logo' style='width: 100px; height: auto;'>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        // Customize the content with the user's display name and verification URL
        content = content.replace("[[name]]", user.getFullName());
        String code = VerificationCodeGenerator.generateVerificationCode();
        content = content.replace("[[code]]", code);

        helper.setText(content, true);

        try {
            Resource res = new FileSystemResource(new File("E:\\image\\CVNest_logo.jpg"));
            helper.addInline("image_logo", res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mailSender.send(message);

        // Delete cache in redis and set new key - value
        String key = "verify:email:" + user.getEmail();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }

        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }

    // LOGIN
    public void updateUserToken(String token, String email) {
        User currentUSer = this.handleGetUserByEmail(email);
        if (currentUSer != null) {
            currentUSer.setRefreshToken(token);
            this.userRepository.save(currentUSer);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    // FORGOT PASSWORD
    public void verifyEmailChangePassword(User user) throws MessagingException {
        String toAddress = user.getEmail();
        String subject = "CVNest Password Reset Verification";
        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f1eae0b9; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border-radius: 10px;'>"
                + "<h1 style='text-align: center; color: #AD7D59;'>CVNest Password Reset Request</h1>"
                + "<p>Hi [[name]],</p>"
                + "<p>We received a request to reset your password for your CVNest account. If this was you, please use the code below to verify your request and proceed with resetting your password:</p>"
                + "<div style='text-align: center; margin: 20px 0;'>"
                + "<p style='font-weight: bold; font-size: 20px;'>[[code]]</p>"
                + "</div>"
                + "<p>This code will expire in 5 minutes for security reasons.</p>"
                + "<p>If you did not request a password reset, please ignore this email. Your account remains secure.</p>"
                + "<p>Thank you,<br>The CVNest Team</p>"
                + "<div style='text-align: right;'>"
                + "<img src='cid:image_logo' alt='CVNest Logo' style='width: 100px; height: auto;'>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        // Customize the content with the user's display name and verification URL
        content = content.replace("[[name]]", user.getFullName());
        String code = VerificationCodeGenerator.generateVerificationCode();
        content = content.replace("[[code]]", code);

        helper.setText(content, true);

        try {
            Resource res = new FileSystemResource(new File("E:\\image\\CVNest_logo.jpg"));
            helper.addInline("image_logo", res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mailSender.send(message);

        String key = "verify:email:changePass:" + user.getEmail();
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }

    public boolean verifyChangePassword(String code, String email) {
        String redisKey = "verify:email:changePass:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    public void resetPassword(User user, String newPassword) {
        String newHashPassword = this.passwordEncoder.encode(newPassword);
        user.setPassword(newHashPassword);
        this.userRepository.save(user);
    }

    // USER
    public void handleUpdateUser(User reqUser) {
        Optional<User> currentUser = this.userRepository.findById(reqUser.getId());
        if (currentUser.isPresent()) {
            currentUser.get().setFullName(reqUser.getFullName());
            currentUser.get().setAge(reqUser.getAge());
            currentUser.get().setAddress(reqUser.getAddress());
            currentUser.get().setGender(reqUser.getGender());
            currentUser.get().setRefreshToken(reqUser.getRefreshToken());

            this.userRepository.save(currentUser.get());
        }
    }
}
