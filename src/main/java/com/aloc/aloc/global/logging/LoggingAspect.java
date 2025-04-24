package com.aloc.aloc.global.logging;

import com.aloc.aloc.webhook.AsyncWebhookNotifier;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
  private final MeterRegistry meterRegistry;
  private final AsyncWebhookNotifier asyncWebhookNotifier;

  @Around(
      "execution(* com.aloc.aloc..controller..*(..)) || execution(* com.aloc.aloc..service..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    String method = joinPoint.getSignature().toShortString();
    log.info("▶️ Start: {}", method);

    Object[] args = joinPoint.getArgs();
    for (Object arg : args) {
      log.debug("  ↳ Param: {}", arg);
    }

    Object result;
    try {
      result = joinPoint.proceed();
    } catch (Throwable ex) {
      log.error("❗️ Exception in {}: {}", method, ex.getMessage(), ex);
      throw ex;
    }

    long duration = System.currentTimeMillis() - start;
    log.info("✅ End: {} ({}ms)", method, duration);

    // ⏱️ 실행 시간 기록
    meterRegistry
        .timer("api.execution.time", "method", method)
        .record(duration, TimeUnit.MILLISECONDS);

    if (duration > 500) {
      asyncWebhookNotifier.notifySlowApi(method, duration);
    }

    return result;
  }
}
