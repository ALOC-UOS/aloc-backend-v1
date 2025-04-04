package com.aloc.aloc.profilebackgroundcolor.repository;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileBackgroundColorRepository
    extends JpaRepository<ProfileBackgroundColor, String> {
  Optional<ProfileBackgroundColor> findByName(String name);

  List<ProfileBackgroundColor> findByType(String type);
}
