package com.bookonthego.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SMSService {
    @Value("${twilio.account.sid}")
    private String twilioSid;

    @Value("${twilio.auth.token}")
    private String twilioToken;

    @Value("${twilio.phone.number}")
    private String senderNumber;

    public void sendSMS(String toNumber, String body) {
        if (twilioSid.isBlank() || twilioToken.isBlank()) {
            log.info("[MOCK SMS] To: {} | Message: {}", toNumber, body);
            return;
        }

        try {
            Twilio.init(twilioSid, twilioToken);

            Message.creator(
                    new com.twilio.type.PhoneNumber(toNumber),
                    new com.twilio.type.PhoneNumber(senderNumber),
                    body
            ).create();

            log.info("SMS sent to {}", toNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to " + toNumber, e);
        }
    }
}
