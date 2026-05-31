package com.hotel.controller;

import com.hotel.dto.ApiResponse;
import com.hotel.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/customer/bills/{bookingId}/invoice")
    public ResponseEntity<?> downloadInvoice(@PathVariable String bookingId, Principal principal) {
        try {
            byte[] pdf = billService.generateInvoicePdf(bookingId, principal.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice-" + bookingId + ".pdf");
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customer/bills/booking/{bookingId}")
    public ResponseEntity<?> getBillForBooking(@PathVariable String bookingId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Bill", billService.getBillForBooking(bookingId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/bills")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        try {
            if (query != null && !query.isEmpty())
                return ResponseEntity.ok(ApiResponse.ok("Bills", billService.searchBills(query, page, size)));
            return ResponseEntity.ok(ApiResponse.ok("Bills", billService.getAllBills(page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/bills/{billId}")
    public ResponseEntity<?> getById(@PathVariable String billId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Bill", billService.getBillById(billId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/bills")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> data) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Bill created", billService.createBill(data)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/bills/{billId}")
    public ResponseEntity<?> update(@PathVariable String billId, @RequestBody Map<String, Object> data) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Bill updated", billService.updateBill(billId, data)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/bills/{bookingId}/invoice")
    public ResponseEntity<?> adminInvoice(@PathVariable String bookingId, Principal principal) {
        try {
            byte[] pdf = billService.generateInvoicePdf(bookingId, principal.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice-" + bookingId + ".pdf");
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
