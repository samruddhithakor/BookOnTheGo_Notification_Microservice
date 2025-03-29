package com.bookonthego.notification.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriber")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Builder.Default
    private boolean subscribed = true;
}