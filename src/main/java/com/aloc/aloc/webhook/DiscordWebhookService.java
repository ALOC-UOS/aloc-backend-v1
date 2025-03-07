package com.aloc.aloc.webhook;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DiscordWebhookService {
  private final RestTemplate restTemplate;

  @Value("${discord.webhook.url}")
  private String discordWebhookUrl;

  public void sendNotification(String message) {
    Map<String, String> request = new HashMap<>();
    request.put("content", message);
    restTemplate.postForObject(discordWebhookUrl, request, String.class);
  }
}
