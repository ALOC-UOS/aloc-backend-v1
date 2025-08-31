package com.aloc.aloc.profilebackgroundcolor.repository;

import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 프로필 배경 색상 레포지토리

// 조회
// 1. findByName 이름으로 조회
// 2. findByType 타입으로 조회
@Repository
public interface ProfileBackgroundColorRepository
    extends JpaRepository<ProfileBackgroundColor, String> {
  Optional<ProfileBackgroundColor> findByName(String name);

  List<ProfileBackgroundColor> findByType(String type);
}
