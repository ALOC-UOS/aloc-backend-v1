package com.aloc.aloc.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Around(
      "execution(* com.aloc.aloc..controller..*(..)) || execution(* com.aloc.aloc..service..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    // 메서드명 + 클래스명
    String method = joinPoint.getSignature().toShortString();
    log.info("▶️ Start: {}", method);

    // 인자 로그
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

    return result;
  }
}
