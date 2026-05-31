package com.hotel.service;

import com.hotel.dto.ComplaintRequest;
import com.hotel.model.*;
import com.hotel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    public Complaint submitComplaint(String username, ComplaintRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Complaint complaint = new Complaint();
        complaint.setComplaintId("CMP" + System.currentTimeMillis());
        complaint.setUser(user);
        complaint.setCategory(Complaint.ComplaintCategory.valueOf(req.getCategory().toUpperCase()));
        complaint.setBookingId(req.getBookingId());
        complaint.setTitle(req.getTitle());
        complaint.setDescription(req.getDescription());
        complaint.setContactPreference(Complaint.ContactPreference.valueOf(req.getContactPreference().toUpperCase()));
        complaint.setStatus(Complaint.ComplaintStatus.OPEN);
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getUserComplaints(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return complaintRepository.findByUser(user);
    }

    public Complaint getComplaintById(String complaintId) {
        return complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public Complaint updateStatus(String complaintId, String status, String response, String actionLog) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(Complaint.ComplaintStatus.valueOf(status.toUpperCase()));
        if (response != null) complaint.setResponse(response);
        if (actionLog != null) {
            String existing = complaint.getActionLog() != null ? complaint.getActionLog() : "";
            complaint.setActionLog(existing + "\n[" + LocalDateTime.now() + "] " + actionLog);
        }
        if (status.equalsIgnoreCase("RESOLVED")) complaint.setResolvedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    public Complaint assignStaff(String complaintId, Long staffId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        complaint.setAssignedStaff(staff);
        complaint.setStatus(Complaint.ComplaintStatus.IN_PROGRESS);
        return complaintRepository.save(complaint);
    }

    public Page<Complaint> getAllComplaints(int page, int size) {
        return complaintRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt")));
    }

    public Page<Complaint> searchComplaints(String query, int page, int size) {
        return complaintRepository.searchComplaints(query, PageRequest.of(page, size));
    }

    public List<Complaint> getStaffComplaints(String username) {
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return complaintRepository.findByAssignedStaff(staff);
    }

    public Complaint confirmResolution(String complaintId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(Complaint.ComplaintStatus.CLOSED);
        return complaintRepository.save(complaint);
    }

    public Complaint reopenComplaint(String complaintId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(Complaint.ComplaintStatus.IN_PROGRESS);
        return complaintRepository.save(complaint);
    }
}
