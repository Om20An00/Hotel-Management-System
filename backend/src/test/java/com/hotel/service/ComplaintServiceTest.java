package com.hotel.service;

import com.hotel.dto.ComplaintRequest;
import com.hotel.model.*;
import com.hotel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplaintServiceTest {

    @Mock private ComplaintRepository complaintRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ComplaintService complaintService;

    private User testUser;
    private Complaint testComplaint;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setCustomerName("Test User");
        testUser.setRole(User.Role.CUSTOMER);

        testComplaint = new Complaint();
        testComplaint.setId(1L);
        testComplaint.setComplaintId("CMP001");
        testComplaint.setUser(testUser);
        testComplaint.setCategory(Complaint.ComplaintCategory.ROOM_ISSUE);
        testComplaint.setTitle("Room AC not working");
        testComplaint.setDescription("The air conditioner in my room is not working properly");
        testComplaint.setContactPreference(Complaint.ContactPreference.EMAIL);
        testComplaint.setStatus(Complaint.ComplaintStatus.OPEN);
    }

    @Test
    void testSubmitComplaintSuccess() {
        ComplaintRequest req = new ComplaintRequest();
        req.setCategory("ROOM_ISSUE");
        req.setTitle("Room AC not working");
        req.setDescription("The air conditioner in my room is not working properly");
        req.setContactPreference("EMAIL");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(complaintRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.submitComplaint("testuser", req);

        assertNotNull(result);
        assertNotNull(result.getComplaintId());
        assertEquals(Complaint.ComplaintStatus.OPEN, result.getStatus());
        verify(complaintRepository, times(1)).save(any());
    }

    @Test
    void testGetUserComplaints() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(complaintRepository.findByUser(testUser)).thenReturn(List.of(testComplaint));

        List<Complaint> result = complaintService.getUserComplaints("testuser");

        assertEquals(1, result.size());
        assertEquals("CMP001", result.get(0).getComplaintId());
    }

    @Test
    void testUpdateStatusToResolved() {
        when(complaintRepository.findByComplaintId("CMP001")).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.updateStatus("CMP001", "RESOLVED", "Issue fixed", "Technician visited");

        assertEquals(Complaint.ComplaintStatus.RESOLVED, result.getStatus());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    void testConfirmResolution() {
        testComplaint.setStatus(Complaint.ComplaintStatus.RESOLVED);
        when(complaintRepository.findByComplaintId("CMP001")).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.confirmResolution("CMP001");

        assertEquals(Complaint.ComplaintStatus.CLOSED, result.getStatus());
    }

    @Test
    void testReopenComplaint() {
        testComplaint.setStatus(Complaint.ComplaintStatus.RESOLVED);
        when(complaintRepository.findByComplaintId("CMP001")).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.reopenComplaint("CMP001");

        assertEquals(Complaint.ComplaintStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void testComplaintNotFound() {
        when(complaintRepository.findByComplaintId("INVALID")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> complaintService.getComplaintById("INVALID"));
        assertEquals("Complaint not found", ex.getMessage());
    }
}
