package com.hotel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String complaintId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "assigned_staff_id")
    private User assignedStaff;

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    private String bookingId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private ContactPreference contactPreference;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    private String response;
    private String resolutionNotes;
    private String actionLog;

    private LocalDateTime submittedAt;
    private LocalDateTime expectedResolutionDate;
    private LocalDateTime resolvedAt;

    public enum ComplaintCategory {
        ROOM_ISSUE, SERVICE_ISSUE, BILLING_ISSUE, OTHER
    }

    public enum ContactPreference {
        CALL, EMAIL
    }

    public enum ComplaintStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    @PrePersist
    public void prePersist() {
        submittedAt = LocalDateTime.now();
        if (status == null) status = ComplaintStatus.OPEN;
        expectedResolutionDate = submittedAt.plusDays(3);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getAssignedStaff() { return assignedStaff; }
    public void setAssignedStaff(User assignedStaff) { this.assignedStaff = assignedStaff; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ContactPreference getContactPreference() { return contactPreference; }
    public void setContactPreference(ContactPreference contactPreference) { this.contactPreference = contactPreference; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getActionLog() { return actionLog; }
    public void setActionLog(String actionLog) { this.actionLog = actionLog; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getExpectedResolutionDate() { return expectedResolutionDate; }
    public void setExpectedResolutionDate(LocalDateTime expectedResolutionDate) { this.expectedResolutionDate = expectedResolutionDate; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
