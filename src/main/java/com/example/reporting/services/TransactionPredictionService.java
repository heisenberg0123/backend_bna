package com.example.reporting.services;

import com.example.reporting.entities.Transaction;
import com.example.reporting.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionPredictionService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final TransactionRepository transactionRepository;

    public TransactionPredictionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    private final RestTemplate restTemplate = new RestTemplate();

    public String predictTransactionType(Long transactionId) {

        Map<String, Object> features = getTransactionFeatures(transactionId);
        try {
            String flaskUrl = "http://localhost:5000/predict";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(features, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
            System.out.println("Flask response: " + response.getBody());
            String type = (String) response.getBody().get("type");
            return type != null ? type : "unknown";
        } catch (Exception e) {
            System.err.println("Prediction error: " + e.getMessage());
            return "unknown";
        }
    }
    public Map<String, Object> getTransactionFeatures(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        // Parse the date as LocalDate
        LocalDate date = LocalDate.parse(transaction.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        int transactionMonth = date.getMonthValue();
        long daysSinceDate = ChronoUnit.DAYS.between(date, LocalDate.now());

        Map<String, Object> features = new HashMap<>();
        features.put("quantity", transaction.getQuantity());
        features.put("date", date); // Store as LocalDate
        features.put("employee_role", transaction.getEmployee().getRole());
        features.put("material_quantity", transaction.getMaterial().getQuantity());
        features.put("transaction_month", transactionMonth); // Add this
        features.put("days_since_date", daysSinceDate); // Add this

        System.out.println("Features for transaction " + transactionId + ": " + features);
        return features;
    }

}