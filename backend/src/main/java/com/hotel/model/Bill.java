package com.hotel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String billId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private BigDecimal roomCharges;
    private BigDecimal serviceCharges;
    private BigDecimal additionalFees;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ElementCollection
    @CollectionTable(name = "bill_service_items", joinColumns = @JoinColumn(name = "bill_id"))
    private List<ServiceItem> serviceItems;

    private LocalDateTime issuedAt;

    public enum PaymentStatus {
        PAID, PENDING, PARTIALLY_PAID
    }

    @Embeddable
    public static class ServiceItem {
        private String serviceDate;
        private String serviceDescription;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal tax;

        public String getServiceDate() { return serviceDate; }
        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }

        public String getServiceDescription() { return serviceDescription; }
        public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTax() { return tax; }
        public void setTax(BigDecimal tax) { this.tax = tax; }
    }

    @PrePersist
    public void prePersist() {
        issuedAt = LocalDateTime.now();
        if (paymentStatus == null) paymentStatus = PaymentStatus.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public BigDecimal getRoomCharges() { return roomCharges; }
    public void setRoomCharges(BigDecimal roomCharges) { this.roomCharges = roomCharges; }

    public BigDecimal getServiceCharges() { return serviceCharges; }
    public void setServiceCharges(BigDecimal serviceCharges) { this.serviceCharges = serviceCharges; }

    public BigDecimal getAdditionalFees() { return additionalFees; }
    public void setAdditionalFees(BigDecimal additionalFees) { this.additionalFees = additionalFees; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public List<ServiceItem> getServiceItems() { return serviceItems; }
    public void setServiceItems(List<ServiceItem> serviceItems) { this.serviceItems = serviceItems; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}
