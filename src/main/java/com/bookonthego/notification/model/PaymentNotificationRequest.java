package com.bookonthego.notification.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentNotificationRequest {
    private String userEmail;
    private String bookingId;
    private String attendeeName;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String venue;
    private String eventId;
}