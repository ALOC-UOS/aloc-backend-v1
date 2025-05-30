package com.aloc.aloc.coin.controller;

import com.aloc.aloc.coin.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CoinController {
  private final CoinService coinService;
}
