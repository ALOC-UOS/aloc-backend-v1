package com.aloc.aloc.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncWebhookNotifier {
  private final DiscordWebhookService discordWebhookService;

  @Async
  public void notifySlowApi(String method, long durationMs) {
    String msg = "⚠️ API 지연 감지: " + method + " (" + durationMs + "ms)";
    discordWebhookService.sendNotification(msg);
  }
}
