package com.bookonthego.notification.service;

import com.bookonthego.notification.model.BookingNotificationRequest;
import com.bookonthego.notification.model.TicketInfo;
import com.bookonthego.notification.repository.SubscriberRepository;
import com.bookonthego.notification.model.PaymentNotificationRequest;
import com.bookonthego.notification.model.EventNotificationRequest;
import com.bookonthego.notification.model.Subscriber;
import org.springframework.core.io.FileSystemResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
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

    public void sendBookingConfirmation(BookingNotificationRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.getUserEmail());
            helper.setSubject("Booking Confirmation - " + request.getEventName());

            // Prepare email content
            Context context = new Context();
            context.setVariable("name", request.getAttendeeName());
            context.setVariable("eventName", request.getEventName());
            context.setVariable("eventDate", request.getEventDate());
            context.setVariable("eventTime", request.getEventTime());
            context.setVariable("venue", request.getVenue());
            context.setVariable("calendarLink", generateGoogleCalendarLink(request));
            context.setVariable("unsubscribeLink", "http://localhost:8082/notify/unsubscribe?email=" + request.getUserEmail());

            String htmlContent = templateEngine.process("booking-confirmation.html", context);
            helper.setText(htmlContent, true);

            // Generate QR & ticket file
            TicketInfo ticketInfo = TicketInfo.builder()
                    .attendeeName(request.getAttendeeName())
                    .bookingId(request.getBookingId())
                    .eventName(request.getEventName())
                    .eventDate(request.getEventDate())
                    .eventTime(request.getEventTime())
                    .venue(request.getVenue())
                    .qrCodeText("BOOKING-" + request.getBookingId())
                    .build();

            File ticketFile = ticketService.generateTicketAsPDF(ticketInfo);
            helper.addAttachment("e-ticket.pdf", ticketFile);

            File qrFile = ticketService.generateQRCodeImage("BOOKING-" + request.getBookingId());
            helper.addInline("qr-code", new FileSystemResource(qrFile));

            mailSender.send(message);
            Files.deleteIfExists(ticketFile.toPath());

            log.info("Booking confirmation email sent to {}", request.getUserEmail());

        } catch (MessagingException e) {
            log.error("Failed to send booking confirmation email", e);
        } catch (Exception e) {
            log.error("Ticket generation failed", e);
        }
    }

    private String generateGoogleCalendarLink(BookingNotificationRequest request) {
        return String.format(
                "https://www.google.com/calendar/render?action=TEMPLATE&text=%s&dates=%sT%s00Z/%sT%s00Z&details=Event+Booking&location=%s",
                request.getEventName(),
                request.getEventDate().replace("-", ""),
                request.getEventTime().replace(":", ""),
                request.getEventDate().replace("-", ""),
                request.getEventTime().replace(":", ""),
                request.getVenue().replace(" ", "+")
        );
    }

    public void sendPaymentConfirmation(PaymentNotificationRequest request) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(request.getUserEmail());
        helper.setSubject("Payment Confirmation - " + request.getEventName());

        Context context = new Context();
        context.setVariable("name", request.getAttendeeName());
        context.setVariable("eventName", request.getEventName());
        context.setVariable("eventDate", request.getEventDate());
        context.setVariable("eventTime", request.getEventTime());
        context.setVariable("venue", request.getVenue());
        context.setVariable("unsubscribeLink", "http://localhost:8082/unsubscribe?email=" + request.getUserEmail());

        String htmlContent = templateEngine.process("payment-success.html", context);
        helper.setText(htmlContent, true);

        TicketInfo ticketInfo = TicketInfo.builder()
                .attendeeName(request.getAttendeeName())
                .bookingId(request.getBookingId())
                .eventName(request.getEventName())
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .venue(request.getVenue())
                .qrCodeText("BOOKING-" + request.getBookingId())
                .build();

        File ticketFile = ticketService.generateTicketAsPDF(ticketInfo);
        helper.addAttachment("e-ticket.html", ticketFile);

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
            helper.setSubject("New Event: " + event.getEventName());

            Context context = new Context();
            context.setVariable("eventName", event.getEventName());
            context.setVariable("eventDate", event.getEventDate());
            context.setVariable("eventTime", event.getEventTime());
            context.setVariable("venue", event.getVenue());
            context.setVariable("promoImageUrl", event.getPromoImageUrl());
            context.setVariable("unsubscribeLink", "http://localhost:8082/unsubscribe?email=" + s.getEmail());

            String html = templateEngine.process("event-created.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Sent new event promo to {}", s.getEmail());

        } catch (Exception e) {
            log.error("Failed to send event promo email to " + s.getEmail(), e);
        }
    }
}



}
