package com.bookonthego.notification.controller;

import com.bookonthego.notification.model.*;
import com.bookonthego.notification.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmailService emailService;
    private final SubscriptionService subscriptionService;
    private final SMSService smsService;

    @PostMapping("/booking")
    public ResponseEntity<String> sendBookingNotification(@RequestBody BookingNotificationRequest request) {
        log.info("Received booking notification for booking ID: {}", request.getBookingId());
        emailService.sendBookingConfirmation(request);
        return ResponseEntity.ok("Booking confirmation email sent successfully.");
    }

    @PostMapping("/payment-success")
    public ResponseEntity<String> sendPaymentSuccessEmail(@RequestBody PaymentNotificationRequest request) {
        log.info("Received payment success notification for booking ID: {}", request.getBookingId());
        emailService.sendPaymentConfirmation(request);
        return ResponseEntity.ok("Payment confirmation email sent successfully.");
    }

    @PostMapping("/event-created")
    public ResponseEntity<String> notifyNewEvent(@RequestBody EventNotificationRequest request) {
        log.info("New event created: {}", request.getEventName());
        emailService.notifySubscribersOfNewEvent(request);
        return ResponseEntity.ok("Promotional event email sent to all subscribers.");
    }

    @GetMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        String msg = subscriptionService.subscribe(email);
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        String msg = subscriptionService.unsubscribe(email);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/sms/send")
public ResponseEntity<String> sendSMS(@RequestParam String to, @RequestParam String message) {
    smsService.sendSMS(to, message);
    return ResponseEntity.ok("SMS sent (or logged in mock mode).");
}

}