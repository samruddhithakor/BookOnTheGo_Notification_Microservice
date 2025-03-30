package com.bookonthego.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionStatusDTO {
    private String email;
    private boolean subscribed;
}