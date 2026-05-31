package com.hotel.dto;

public class ComplaintRequest {
    private String category;
    private String bookingId;
    private String title;
    private String description;
    private String contactPreference;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContactPreference() { return contactPreference; }
    public void setContactPreference(String contactPreference) { this.contactPreference = contactPreference; }
}
