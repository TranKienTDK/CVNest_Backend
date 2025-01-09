package com.gr2.CVNest.controller;

import com.gr2.CVNest.service.EmailService;
import com.gr2.CVNest.service.SubscriberService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    public String sendSimpleEmail() {
//        this.emailService.sendEmailSync("kien8b2003@gmail.com", "Test send email", "<h1>Test <b> send </b> email</h1>",
//                false, true);
//        this.emailService.sendEmailFromTemplateSync("kien8b2003@gmail.com", "Test send email", "job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }
}
