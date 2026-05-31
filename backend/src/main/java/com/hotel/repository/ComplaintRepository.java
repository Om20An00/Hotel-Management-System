package com.hotel.repository;

import com.hotel.model.Complaint;
import com.hotel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintId(String complaintId);
    List<Complaint> findByUser(User user);
    List<Complaint> findByAssignedStaff(User staff);
    Page<Complaint> findByUser(User user, Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(LOWER(c.user.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.complaintId) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Complaint> searchComplaints(@Param("query") String query, Pageable pageable);

    Page<Complaint> findByStatus(Complaint.ComplaintStatus status, Pageable pageable);
    Page<Complaint> findByCategory(Complaint.ComplaintCategory category, Pageable pageable);
    Page<Complaint> findByAssignedStaff(User staff, Pageable pageable);
}
