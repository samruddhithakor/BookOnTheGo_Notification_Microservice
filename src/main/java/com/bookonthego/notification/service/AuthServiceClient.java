package com.bookonthego.notification.service;

import com.bookonthego.notification.dto.SubscriptionStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${microservices.auth-service-url}")
    private String authServiceUrl;

    public ResponseEntity<String> subscribe(String email, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = authServiceUrl + "/auth/subscribe?email=" + email;
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> unsubscribe(String email, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = authServiceUrl + "/auth/unsubscribe?email=" + email;
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<SubscriptionStatusDTO> getSubscriptionStatus(String email) {
        String url = authServiceUrl + "/auth/subscription-status?email=" + email;
        return restTemplate.getForEntity(url, SubscriptionStatusDTO.class);
    }
}
