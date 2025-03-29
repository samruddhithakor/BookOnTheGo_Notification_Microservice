// 📁 model/TicketInfo.java
package com.bookonthego.notification.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketInfo {
    private String bookingId;
    private String attendeeName;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String venue;
    private String qrCodeText;
}