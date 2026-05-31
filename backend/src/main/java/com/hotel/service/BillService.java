package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Bill> getAllBills(int page, int size) {
        return billRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "issuedAt")));
    }

    public Page<Bill> searchBills(String query, int page, int size) {
        return billRepository.searchBills(query, PageRequest.of(page, size));
    }

    public Bill getBillById(String billId) {
        return billRepository.findByBillId(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public Bill getBillForBooking(String bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return billRepository.findByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Bill not found for this booking"));
    }

    public Bill createBill(Map<String, Object> data) {
        User user = userRepository.findByUserId((String) data.get("userId"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        Bill bill = new Bill();
        bill.setBillId("BILL" + System.currentTimeMillis());
        bill.setUser(user);
        bill.setRoomCharges(new BigDecimal(data.get("roomCharges").toString()));
        bill.setServiceCharges(new BigDecimal(data.getOrDefault("serviceCharges", "0").toString()));
        bill.setAdditionalFees(new BigDecimal(data.getOrDefault("additionalFees", "0").toString()));
        bill.setTaxAmount(new BigDecimal(data.getOrDefault("taxAmount", "0").toString()));
        bill.setDiscount(new BigDecimal(data.getOrDefault("discount", "0").toString()));
        BigDecimal total = bill.getRoomCharges().add(bill.getServiceCharges())
                .add(bill.getAdditionalFees()).add(bill.getTaxAmount()).subtract(bill.getDiscount());
        bill.setTotalAmount(total);
        bill.setPaymentStatus(Bill.PaymentStatus.PENDING);
        return billRepository.save(bill);
    }

    public Bill updateBill(String billId, Map<String, Object> data) {
        Bill bill = billRepository.findByBillId(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        if (data.containsKey("roomCharges")) bill.setRoomCharges(new BigDecimal(data.get("roomCharges").toString()));
        if (data.containsKey("serviceCharges")) bill.setServiceCharges(new BigDecimal(data.get("serviceCharges").toString()));
        if (data.containsKey("additionalFees")) bill.setAdditionalFees(new BigDecimal(data.get("additionalFees").toString()));
        if (data.containsKey("taxAmount")) bill.setTaxAmount(new BigDecimal(data.get("taxAmount").toString()));
        if (data.containsKey("discount")) bill.setDiscount(new BigDecimal(data.get("discount").toString()));
        BigDecimal total = bill.getRoomCharges().add(bill.getServiceCharges())
                .add(bill.getAdditionalFees()).add(bill.getTaxAmount()).subtract(bill.getDiscount());
        bill.setTotalAmount(total);
        return billRepository.save(bill);
    }

    public byte[] generateInvoicePdf(String bookingId, String username) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getUsername().equals(username) && !isAdmin(username))
            throw new RuntimeException("Unauthorized");
        if (booking.getPaymentStatus() != Booking.PaymentStatus.PAID)
            throw new RuntimeException("Invoice is only available for paid bookings");

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("GRAND HOTEL").setBold().setFontSize(20));
            doc.add(new Paragraph("123 Hotel Street, City - 123456").setFontSize(10));
            doc.add(new Paragraph("Email: support@grandhotel.com | Phone: +91-9876543210").setFontSize(10));
            doc.add(new Paragraph("---------------------------------------------------"));
            doc.add(new Paragraph("INVOICE").setBold().setFontSize(16));
            doc.add(new Paragraph("Invoice Number: INV-" + booking.getBookingId()));
            doc.add(new Paragraph("Booking ID: " + booking.getBookingId()));
            doc.add(new Paragraph("Transaction ID: " + booking.getTransactionId()));
            doc.add(new Paragraph("Invoice Date: " + java.time.LocalDate.now()));
            doc.add(new Paragraph("---------------------------------------------------"));
            doc.add(new Paragraph("Customer Details").setBold());
            doc.add(new Paragraph("Name: " + booking.getUser().getCustomerName()));
            doc.add(new Paragraph("Email: " + booking.getUser().getEmail()));
            doc.add(new Paragraph("Mobile: " + booking.getUser().getCountryCode() + " " + booking.getUser().getMobileNumber()));
            doc.add(new Paragraph("---------------------------------------------------"));
            doc.add(new Paragraph("Room Details").setBold());
            doc.add(new Paragraph("Room Number: " + booking.getRoom().getRoomNumber()));
            doc.add(new Paragraph("Room Type: " + booking.getRoom().getRoomType()));
            doc.add(new Paragraph("Check-in: " + booking.getCheckInDate()));
            doc.add(new Paragraph("Check-out: " + booking.getCheckOutDate()));
            long nights = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            doc.add(new Paragraph("Number of Nights: " + nights));
            doc.add(new Paragraph("Guests: " + booking.getNumberOfAdults() + " Adults, " + booking.getNumberOfChildren() + " Children"));
            doc.add(new Paragraph("---------------------------------------------------"));
            doc.add(new Paragraph("Payment Details").setBold());
            doc.add(new Paragraph("Base Price: Rs. " + booking.getBasePrice()));
            doc.add(new Paragraph("Tax (18%): Rs. " + booking.getTaxAmount()));
            doc.add(new Paragraph("Total Amount Paid: Rs. " + booking.getTotalAmount()).setBold());
            doc.add(new Paragraph("Payment Method: " + booking.getPaymentMethod()));
            doc.add(new Paragraph("---------------------------------------------------"));
            doc.add(new Paragraph("Thank you for choosing Grand Hotel!").setItalic());

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Invoice generation failed: " + e.getMessage());
        }
    }

    private boolean isAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(u -> u.getRole() == User.Role.ADMIN)
                .orElse(false);
    }
}
