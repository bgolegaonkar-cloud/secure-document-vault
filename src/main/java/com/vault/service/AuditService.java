package com.vault.service;

import com.vault.model.AuditLog;
import com.vault.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Save a log entry — called after every important action
    public void log(String username, String action, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);     // "LOGIN", "UPLOAD", "DOWNLOAD"
        log.setDetails(details);   // "Uploaded file: resume.pdf"
        auditLogRepository.save(log);
    }

    // Get all logs — for admin panel
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    // Get logs for one specific user
    public List<AuditLog> getLogsByUsername(String username) {
        return auditLogRepository.findByUsername(username);
    }
}