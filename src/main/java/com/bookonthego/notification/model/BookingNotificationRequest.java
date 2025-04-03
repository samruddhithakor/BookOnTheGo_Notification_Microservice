package com.bookonthego.notification.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingNotificationRequest {
    private String userEmail;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String venue;
    private String bookingId;
}