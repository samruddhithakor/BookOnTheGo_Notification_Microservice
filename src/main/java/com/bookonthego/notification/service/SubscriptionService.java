package com.bookonthego.notification.service;

import com.bookonthego.notification.model.Subscriber;
import com.bookonthego.notification.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriberRepository subscriberRepository;

    public String subscribe(String email) {
    return subscriberRepository.findByEmail(email).map(subscriber -> {
        if (subscriber.isSubscribed()) {
            return "You're already subscribed.";
        }
        subscriber.setSubscribed(true);
        subscriberRepository.save(subscriber);
        log.info("Re-subscribed: {}", email);
        return "You have been re-subscribed!";
    }).orElseGet(() -> {
        Subscriber newSubscriber = Subscriber.builder()
                .email(email)
                .subscribed(true)
                .build();
        subscriberRepository.save(newSubscriber);
        log.info("New subscriber added: {}", email);
        return "Subscription successful. You'll now receive event updates!";
    });
}


    public String unsubscribe(String email) {
        return subscriberRepository.findByEmail(email).map(sub -> {
            sub.setSubscribed(false);
            subscriberRepository.save(sub);
            log.info("Unsubscribed: {}", email);
            return "You've been unsubscribed from promotional emails.";
        }).orElse("Email not found or already unsubscribed.");
    }
}