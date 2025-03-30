package com.bookonthego.notification.service;

import com.bookonthego.notification.model.BookingNotificationRequest;
import com.bookonthego.notification.model.EventNotificationRequest;
import com.bookonthego.notification.model.TicketInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; 
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final RestTemplate restTemplate;

    @Value("${booking-service.url}")
    private String bookingServiceUrl; // Use values from configuration
    @Value("${event-service.url}")
    private String eventServiceUrl;   // Use values from configuration

    public File generateTicketAsPDF(BookingNotificationRequest request) throws Exception {
        // Fetch Event and Booking Data
        EventNotificationRequest event = fetchEventInfo(request.getEventId());
        BookingNotificationRequest booking = fetchBookingInfo(request.getBookingId());

        TicketInfo ticketInfo = TicketInfo.builder()
                .bookingId(request.getBookingId())
                .attendeeName(booking.getAttendeeName())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .venue(event.getVenue())
                .qrCodeText("BOOKING-" + request.getBookingId())
                .build();

        return generateTicketAsPDF(ticketInfo);
    }

    private EventNotificationRequest fetchEventInfo(String eventId) {
        try {
            return restTemplate.getForObject(eventServiceUrl + "/event/" + eventId, EventNotificationRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch event info: " + e.getMessage());
        }
    }

    private BookingNotificationRequest fetchBookingInfo(String bookingId) {
        try {
            return restTemplate.getForObject(bookingServiceUrl + "/booking/" + bookingId, BookingNotificationRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch booking info: " + e.getMessage());
        }
    }

    public File generateTicketAsPDF(TicketInfo info) throws Exception {
        File pdfFile = File.createTempFile("ticket_", ".pdf");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(info.getQrCodeText(), BarcodeFormat.QR_CODE, 150, 150);
        Path qrPath = Files.createTempFile("qr_", ".png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrPath);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("E-Ticket: " + info.getBookingId(), titleFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Name: " + info.getAttendeeName(), textFont));
        document.add(new Paragraph("Event: " + info.getEventName(), textFont));
        document.add(new Paragraph("Date: " + info.getEventDate(), textFont));
        document.add(new Paragraph("Time: " + info.getEventTime(), textFont));
        document.add(new Paragraph("Venue: " + info.getVenue(), textFont));
        document.add(Chunk.NEWLINE);

        Image qrImg = Image.getInstance(qrPath.toAbsolutePath().toString());
        qrImg.setAlignment(Image.ALIGN_CENTER);
        qrImg.scaleToFit(150, 150);
        document.add(qrImg);

        document.close();
        Files.deleteIfExists(qrPath);

        return pdfFile;
    }

    public File generateQRCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        Path qrPath = Files.createTempFile("qr_", ".png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrPath);
        return qrPath.toFile();
    }
}
