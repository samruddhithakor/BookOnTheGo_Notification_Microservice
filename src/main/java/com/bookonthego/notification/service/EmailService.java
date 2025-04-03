package com.bookonthego.notification.service;

import com.bookonthego.notification.model.BookingNotificationRequest;
import com.bookonthego.notification.model.PaymentNotificationRequest;
import com.bookonthego.notification.model.EventNotificationRequest;
import com.bookonthego.notification.model.Subscriber;
import com.bookonthego.notification.model.TicketInfo;
import com.bookonthego.notification.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TicketService ticketService;
    private final SubscriberRepository subscriberRepository;

    private static final String BASE_URL = "http://localhost:8084/notify";

    public void sendBookingConfirmation(BookingNotificationRequest request) {
        try {
            EventNotificationRequest event = EventNotificationRequest.builder()
                    .eventId(request.getEventId())
                    .eventName(request.getEventName())
                    .eventDate(request.getEventDate())
                    .eventTime(request.getEventTime())
                    .venue(request.getVenue())
                    .build();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.getUserEmail());
            helper.setSubject("Booking Confirmation - " + event.getEventName());

            Context context = new Context();
            context.setVariable("name", request.getAttendeeName());
            context.setVariable("eventName", event.getEventName());
            context.setVariable("eventDate", event.getEventDate());
            context.setVariable("eventTime", event.getEventTime());
            context.setVariable("venue", event.getVenue());
            context.setVariable("calendarLink", generateGoogleCalendarLink(event));
            context.setVariable("unsubscribeLink", BASE_URL + "/unsubscribe?email=" + request.getUserEmail());

            String htmlContent = templateEngine.process("booking-confirmation.html", context);
            helper.setText(htmlContent, true);

            TicketInfo ticketInfo = TicketInfo.builder()
                    .attendeeName(request.getAttendeeName())
                    .bookingId(request.getBookingId())
                    .eventName(event.getEventName())
                    .eventDate(event.getEventDate())
                    .eventTime(event.getEventTime())
                    .venue(event.getVenue())
                    .qrCodeText("BOOKING-" + request.getBookingId())
                    .build();

            File ticketFile = ticketService.generateTicketAsPDF(ticketInfo);
            helper.addAttachment("e-ticket.pdf", ticketFile);

            File qrFile = ticketService.generateQRCodeImage("BOOKING-" + request.getBookingId());
            helper.addInline("qr-code", qrFile);

            mailSender.send(message);
            Files.deleteIfExists(ticketFile.toPath());

            log.info("Booking confirmation email sent to {}", request.getUserEmail());

        } catch (MessagingException e) {
            log.error("Failed to send booking confirmation email", e);
        } catch (Exception e) {
            log.error("Ticket generation or service call failed", e);
        }
    }

    private String generateGoogleCalendarLink(EventNotificationRequest event) {
        return String.format(
                "https://www.google.com/calendar/render?action=TEMPLATE&text=%s&dates=%sT%s00Z/%sT%s00Z&details=Event+Booking&location=%s",
                event.getEventName(),
                event.getEventDate().replace("-", ""),
                event.getEventTime().replace(":", ""),
                event.getEventDate().replace("-", ""),
                event.getEventTime().replace(":", ""),
                event.getVenue().replace(" ", "+")
        );
    }

    public void sendPaymentConfirmation(PaymentNotificationRequest request) {
        try {
            EventNotificationRequest event = EventNotificationRequest.builder()
                    .eventId(request.getEventId())
                    .eventName(request.getEventName())
                    .eventDate(request.getEventDate())
                    .eventTime(request.getEventTime())
                    .venue(request.getVenue())
                    .build();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.getUserEmail());
            helper.setSubject("Payment Confirmation - " + event.getEventName());

            Context context = new Context();
            context.setVariable("name", request.getAttendeeName());
            context.setVariable("eventName", event.getEventName());
            context.setVariable("eventDate", event.getEventDate());
            context.setVariable("eventTime", event.getEventTime());
            context.setVariable("venue", event.getVenue());
            context.setVariable("unsubscribeLink", BASE_URL + "/unsubscribe?email=" + request.getUserEmail());

            String htmlContent = templateEngine.process("payment-success.html", context);
            helper.setText(htmlContent, true);

            TicketInfo ticketInfo = TicketInfo.builder()
                    .attendeeName(request.getAttendeeName())
                    .bookingId(request.getBookingId())
                    .eventName(event.getEventName())
                    .eventDate(event.getEventDate())
                    .eventTime(event.getEventTime())
                    .venue(event.getVenue())
                    .qrCodeText("BOOKING-" + request.getBookingId())
                    .build();

            File ticketFile = ticketService.generateTicketAsPDF(ticketInfo);
            helper.addAttachment("e-ticket.pdf", ticketFile);

            mailSender.send(message);
            Files.deleteIfExists(ticketFile.toPath());

            log.info("Payment confirmation email sent to {}", request.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to send payment confirmation email", e);
        }
    }

    public void notifySubscribersOfNewEvent(EventNotificationRequest event) {
    List<Subscriber> subscribers = subscriberRepository.findAll()
            .stream()
            .filter(Subscriber::isSubscribed)
            .toList();

    for (Subscriber s : subscribers) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(s.getEmail());
            helper.setSubject("🎉 New Event: " + event.getEventName());

            Context context = new Context();
            context.setVariable("eventName", event.getEventName());
            context.setVariable("eventDate", event.getEventDate());
            context.setVariable("eventTime", event.getEventTime());
            context.setVariable("venue", event.getVenue());
            context.setVariable("unsubscribeLink", BASE_URL + "/unsubscribe?email=" + s.getEmail());

            String html = templateEngine.process("event-created.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Sent NEW EVENT promo to {}", s.getEmail());

        } catch (Exception e) {
            log.error("Failed to send NEW EVENT promo email to " + s.getEmail(), e);
        }
    }
}

public void notifySubscribersOfUpdatedEvent(EventNotificationRequest event) {
    List<Subscriber> subscribers = subscriberRepository.findAll()
            .stream()
            .filter(Subscriber::isSubscribed)
            .toList();

    for (Subscriber s : subscribers) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(s.getEmail());
            helper.setSubject("📢 Event Updated: " + event.getEventName());

            Context context = new Context();
            context.setVariable("eventName", event.getEventName());
            context.setVariable("eventDate", event.getEventDate());
            context.setVariable("eventTime", event.getEventTime());
            context.setVariable("venue", event.getVenue());
            context.setVariable("unsubscribeLink", BASE_URL + "/unsubscribe?email=" + s.getEmail());

            String html = templateEngine.process("event-updated.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Sent updated event email to {}", s.getEmail());

        } catch (Exception e) {
            log.error("Failed to send updated event email to " + s.getEmail(), e);
        }
    }
}


}
