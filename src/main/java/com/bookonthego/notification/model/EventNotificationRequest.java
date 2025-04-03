package com.bookonthego.notification.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNotificationRequest {
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String venue;
}