package com.aloc.aloc.report.repository;

import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.entity.UserReport;
import com.aloc.aloc.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

  Optional<UserReport> findByUserAndReport(User user, Report report);
}
