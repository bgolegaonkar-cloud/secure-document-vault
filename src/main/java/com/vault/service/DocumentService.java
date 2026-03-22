package com.vault.service;

import com.vault.model.Document;
import com.vault.model.User;
import com.vault.repository.DocumentRepository;
import com.vault.util.AesEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AesEncryptionUtil aesEncryptionUtil;

    @Autowired
    private AuditService auditService;

    // ── UPLOAD ────────────────────────────────────────────
    // Called when user uploads a file
    public Document uploadDocument(MultipartFile file,
                                   String username) throws Exception {
        // Step 1: Find the user in database
        User owner = userService.findByUsername(username);

        // Step 2: Read file as bytes
        byte[] fileBytes = file.getBytes();

        // Step 3: Encrypt file bytes using AES
        // fileBytes → [AES + secret key] → encrypted Base64 string
        String encryptedContent = aesEncryptionUtil.encrypt(fileBytes);

        // Step 4: Create document object
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setEncryptedContent(encryptedContent); // save encrypted
        document.setOwner(owner);

        // Step 5: Save to MySQL
        Document saved = documentRepository.save(document);

        // Step 6: Log this action
        auditService.log(username, "UPLOAD",
            "Uploaded file: " + file.getOriginalFilename());

        return saved;
    }

    // ── DOWNLOAD ──────────────────────────────────────────
    // Called when user downloads a file
    public byte[] downloadDocument(Long documentId,
                                   String username) throws Exception {
        // Step 1: Find document in database
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found!"));

        // Step 2: Check if this user owns the document
        if (!document.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Access denied!");
        }

        // Step 3: Log this action
        auditService.log(username, "DOWNLOAD",
            "Downloaded file: " + document.getFileName());

        // Step 4: Decrypt the file using AES
        // encrypted Base64 string → [AES + same key] → original bytes
        return aesEncryptionUtil.decrypt(document.getEncryptedContent());
    }

    // ── GET USER DOCUMENTS ────────────────────────────────
    // Returns list of documents for one user
    public List<Document> getUserDocuments(String username) {
        User owner = userService.findByUsername(username);
        return documentRepository.findByOwner(owner);
    }

    // ── GET ALL DOCUMENTS ─────────────────────────────────
    // Admin only — returns all documents
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}