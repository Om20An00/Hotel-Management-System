package com.hotel.controller;

import com.hotel.dto.*;
import com.hotel.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/customer/complaints")
    public ResponseEntity<?> submit(@RequestBody ComplaintRequest req, Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Your complaint has been successfully submitted.", complaintService.submitComplaint(principal.getName(), req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/complaints")
    public ResponseEntity<?> getUserComplaints(Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Complaints", complaintService.getUserComplaints(principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/complaints/{complaintId}")
    public ResponseEntity<?> getComplaint(@PathVariable String complaintId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Complaint", complaintService.getComplaintById(complaintId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/customer/complaints/{complaintId}/confirm-resolution")
    public ResponseEntity<?> confirmResolution(@PathVariable String complaintId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Complaint closed", complaintService.confirmResolution(complaintId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/customer/complaints/{complaintId}/reopen")
    public ResponseEntity<?> reopen(@PathVariable String complaintId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Complaint reopened", complaintService.reopenComplaint(complaintId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/complaints")
    public ResponseEntity<?> adminGetAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        try {
            if (query != null && !query.isEmpty())
                return ResponseEntity.ok(ApiResponse.ok("Complaints", complaintService.searchComplaints(query, page, size)));
            return ResponseEntity.ok(ApiResponse.ok("Complaints", complaintService.getAllComplaints(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/complaints/{complaintId}/assign")
    public ResponseEntity<?> assign(@PathVariable String complaintId, @RequestBody Map<String, Long> body) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Complaint assigned", complaintService.assignStaff(complaintId, body.get("staffId"))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/complaints/{complaintId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String complaintId, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Status updated", complaintService.updateStatus(complaintId, body.get("status"), body.get("response"), body.get("actionLog"))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/staff/complaints")
    public ResponseEntity<?> staffComplaints(Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Staff complaints", complaintService.getStaffComplaints(principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/staff/complaints/{complaintId}/status")
    public ResponseEntity<?> staffUpdateStatus(@PathVariable String complaintId, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Status updated", complaintService.updateStatus(complaintId, body.get("status"), body.get("response"), body.get("actionLog"))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
