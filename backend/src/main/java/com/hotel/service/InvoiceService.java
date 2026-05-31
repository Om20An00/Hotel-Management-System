package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.repository.BookingRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    @Autowired
    private BookingRepository bookingRepository;

    public byte[] generateInvoice(String bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentStatus() != Booking.PaymentStatus.PAID) {
            throw new RuntimeException("Invoice is only available for paid bookings");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            doc.add(new Paragraph("GRAND HOTEL")
                    .setFontSize(24).setBold().setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));
            doc.add(new Paragraph("Invoice")
                    .setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(" "));

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            infoTable.addCell(new Cell().add(new Paragraph("Invoice Number: INV-" + booking.getBookingId())).setBorder(null));
            infoTable.addCell(new Cell().add(new Paragraph("Booking ID: " + booking.getBookingId())).setBorder(null));
            infoTable.addCell(new Cell().add(new Paragraph("Customer: " + booking.getUser().getCustomerName())).setBorder(null));
            infoTable.addCell(new Cell().add(new Paragraph("Email: " + booking.getUser().getEmail())).setBorder(null));
            infoTable.addCell(new Cell().add(new Paragraph("Mobile: " + booking.getUser().getCountryCode() + " " + booking.getUser().getMobileNumber())).setBorder(null));
            infoTable.addCell(new Cell().add(new Paragraph("Transaction ID: " + booking.getTransactionId())).setBorder(null));
            doc.add(infoTable);
            doc.add(new Paragraph(" "));

            Table detailTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            detailTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            detailTable.addHeaderCell(new Cell().add(new Paragraph("Details").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            detailTable.addCell("Room Type");
            detailTable.addCell(booking.getRoom().getRoomType().name());
            detailTable.addCell("Room Number");
            detailTable.addCell(booking.getRoom().getRoomNumber());
            detailTable.addCell("Check-in Date");
            detailTable.addCell(booking.getCheckInDate().format(fmt));
            detailTable.addCell("Check-out Date");
            detailTable.addCell(booking.getCheckOutDate().format(fmt));
            long nights = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            detailTable.addCell("Number of Nights");
            detailTable.addCell(String.valueOf(nights));
            detailTable.addCell("Guests");
            detailTable.addCell(booking.getNumberOfAdults() + " Adults, " + booking.getNumberOfChildren() + " Children");
            detailTable.addCell("Payment Method");
            detailTable.addCell(booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "N/A");
            doc.add(detailTable);
            doc.add(new Paragraph(" "));

            Table amtTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            amtTable.addCell("Base Price");
            amtTable.addCell("Rs. " + booking.getBasePrice());
            amtTable.addCell("GST (18%)");
            amtTable.addCell("Rs. " + booking.getTaxAmount());
            Cell totalLabel = new Cell().add(new Paragraph("Total Amount").setBold());
            Cell totalValue = new Cell().add(new Paragraph("Rs. " + booking.getTotalAmount()).setBold());
            amtTable.addCell(totalLabel);
            amtTable.addCell(totalValue);
            doc.add(amtTable);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Grand Hotel | support@grandhotel.com | +91-9000000000")
                    .setFontSize(9).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.GRAY));

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Invoice generation failed: " + e.getMessage());
        }
        return baos.toByteArray();
    }
}
