package com.vault.controller;

import com.vault.model.Document;
import com.vault.service.AuditService;
import com.vault.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private AuditService auditService;

    // ── UPLOAD ────────────────────────────────────────────
    // POST /api/documents/upload
    // Requires JWT token in header
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            // authentication.getName() = username from JWT token
            String username = authentication.getName();
            Document saved = documentService.uploadDocument(file, username);

            return ResponseEntity.ok(Map.of(
                "message", "File uploaded and encrypted successfully!",
                "documentId", saved.getId(),
                "fileName", saved.getFileName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    // ── MY DOCUMENTS ──────────────────────────────────────
    // GET /api/documents/my-documents
    // Returns all documents for logged in user
    @GetMapping("/my-documents")
    public ResponseEntity<?> getMyDocuments(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Document> documents =
                documentService.getUserDocuments(username);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    // ── DOWNLOAD ──────────────────────────────────────────
    // GET /api/documents/download/{id}
    // Returns decrypted file bytes
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            byte[] fileBytes =
                documentService.downloadDocument(id, username);

            // Tell browser this is a file download
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=document")
                .body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ── ADMIN — ALL DOCUMENTS ─────────────────────────────
    // GET /api/documents/admin/all
    // Only ROLE_ADMIN can access this
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    // ── ADMIN — AUDIT LOGS ────────────────────────────────
    // GET /api/documents/admin/audit-logs
    @GetMapping("/admin/audit-logs")
    public ResponseEntity<?> getAuditLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }
}