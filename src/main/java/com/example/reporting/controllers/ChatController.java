package com.example.reporting.controllers;

import com.example.reporting.repositories.EmployeeRepository;
import com.example.reporting.repositories.MaterialRepository;
import com.example.reporting.repositories.TransactionRepository;
import com.example.reporting.services.GeneralChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    public final TransactionRepository transactionRepository;
    public final EmployeeRepository employeeRepository;
    public final MaterialRepository materialRepository;
    public final GeneralChatService chatService;

    public ChatController(TransactionRepository transactionRepository, EmployeeRepository employeeRepository, MaterialRepository materialRepository, GeneralChatService chatService) {
        this.transactionRepository = transactionRepository;
        this.employeeRepository = employeeRepository;
        this.materialRepository = materialRepository;
        this.chatService = chatService;
    }

    @GetMapping("/count/employees")
    public Map<String, Long> getEmployeeCount() {
        return Map.of("count", employeeRepository.count());
    }

    @GetMapping("/count/materials")
    public Map<String, Long> getMaterialCount() {
        return Map.of("count", materialRepository.count());
    }

    @GetMapping("/count/transactions")
    public Map<String, Long> getTransactionCount() {
        return Map.of("count", transactionRepository.count());
    }

    @PostMapping("/chat")
    public Map<String, String> handleChat(@RequestBody Map<String, String> request) {
        String command = request.get("command");
        String reply = chatService.processCommand(command);
        return Map.of("reply", reply);
    }
}