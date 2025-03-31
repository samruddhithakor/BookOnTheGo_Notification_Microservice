package com.bookonthego.notification.controller;

import com.bookonthego.notification.model.*;
import com.bookonthego.notification.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmailService emailService;
    private final SubscriptionService subscriptionService;
    private final AuthServiceClient authServiceClient;

    // Endpoint to send booking notifications
    @PostMapping("/booking")
    public ResponseEntity<String> sendBookingNotification(@RequestBody BookingNotificationRequest request) {
        log.info("Received booking notification for booking ID: {}", request.getBookingId());

        // Sending booking confirmation email
        emailService.sendBookingConfirmation(request);

        return ResponseEntity.ok("Booking confirmation email sent successfully.");
    }

    // Endpoint to send payment success notifications
    @PostMapping("/payment-success")
    public ResponseEntity<String> sendPaymentSuccessEmail(@RequestBody PaymentNotificationRequest request) {
        log.info("Received payment success notification for booking ID: {}", request.getBookingId());

        // Sending payment confirmation email
        emailService.sendPaymentConfirmation(request);

        return ResponseEntity.ok("Payment confirmation email sent successfully.");
    }

    // Endpoint to notify new event creation
    @PostMapping("/event-created")
    public ResponseEntity<String> notifyNewEvent(@RequestBody EventNotificationRequest request) {
        log.info("New event created: {}", request.getEventName());

        // Sending new event email notification
        emailService.notifySubscribersOfNewEvent(request);

        return ResponseEntity.ok("Promotional event email sent to all subscribers.");
    }

    // Endpoint to notify event update (edited event)
   @PostMapping("/event-updated")
public ResponseEntity<String> notifyEventUpdated(@RequestBody EventNotificationRequest request) {
    log.info("Event updated: {}", request.getEventName());


     emailService.notifySubscribersOfUpdatedEvent(request);

    return ResponseEntity.ok("Event updated notification sent to all subscribers via SMS.");
}


   @GetMapping("/subscribe")
public ResponseEntity<String> subscribe(@RequestParam String email) {
    String result = subscriptionService.subscribe(email);
    return ResponseEntity.ok(result);
}

@GetMapping("/unsubscribe")
public ResponseEntity<String> unsubscribe(@RequestParam String email) {
    String result = subscriptionService.unsubscribe(email);
    return ResponseEntity.ok(result);
}


}
