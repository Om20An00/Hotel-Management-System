package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingDto {

    public static class BookingRequest {
        private Long roomId;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private int numberOfAdults;
        private int numberOfChildren;
        private String specialRequests;
        private String paymentMethod;

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

        public LocalDate getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

        public int getNumberOfAdults() { return numberOfAdults; }
        public void setNumberOfAdults(int numberOfAdults) { this.numberOfAdults = numberOfAdults; }

        public int getNumberOfChildren() { return numberOfChildren; }
        public void setNumberOfChildren(int numberOfChildren) { this.numberOfChildren = numberOfChildren; }

        public String getSpecialRequests() { return specialRequests; }
        public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class PaymentRequest {
        private String bookingId;
        private String cardholderName;
        private String cardNumber;
        private String expiryDate;
        private String cvv;
        private String billingAddress;
        private String paymentMethod;

        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }

        public String getCardholderName() { return cardholderName; }
        public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

        public String getExpiryDate() { return expiryDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }

        public String getBillingAddress() { return billingAddress; }
        public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class AdminBookingRequest {
        private String customerId;
        private Long roomId;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private int numberOfAdults;
        private int numberOfChildren;
        private String paymentMethod;
        private BigDecimal depositAmount;

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

        public LocalDate getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

        public int getNumberOfAdults() { return numberOfAdults; }
        public void setNumberOfAdults(int numberOfAdults) { this.numberOfAdults = numberOfAdults; }

        public int getNumberOfChildren() { return numberOfChildren; }
        public void setNumberOfChildren(int numberOfChildren) { this.numberOfChildren = numberOfChildren; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public BigDecimal getDepositAmount() { return depositAmount; }
        public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }
    }

    public static class UpdateBookingRequest {
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private int numberOfAdults;
        private int numberOfChildren;
        private Long roomId;
        private String specialRequests;

        public LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

        public LocalDate getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

        public int getNumberOfAdults() { return numberOfAdults; }
        public void setNumberOfAdults(int numberOfAdults) { this.numberOfAdults = numberOfAdults; }

        public int getNumberOfChildren() { return numberOfChildren; }
        public void setNumberOfChildren(int numberOfChildren) { this.numberOfChildren = numberOfChildren; }

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public String getSpecialRequests() { return specialRequests; }
        public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    }
}
