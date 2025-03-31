package com.bookonthego.notification.model;

import lombok.*;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscribers", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean subscribed;
}
