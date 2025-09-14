package com.example.reporting.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeneralChatService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String processCommand(String command) {
        Map<String, Object> request = Map.of("sender", "user", "message", command);
        Map[] response = restTemplate.postForObject(
                "http://localhost:5005/webhooks/rest/webhook",
                request,
                Map[].class
        );
        return response != null && response.length > 0 ?
                (String) response[0].get("text") :
                "Sorry, I didn’t get that. Try again!";
    }
}