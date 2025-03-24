package com.aloc.aloc.coin.repository;

import com.aloc.aloc.coin.entity.CoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long> {}
