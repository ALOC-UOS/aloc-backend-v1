package com.aloc.aloc.report.repository;

import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  @Query(
      "SELECT r FROM Report r WHERE "
          + "r.reportState != :deletedState "
          + "ORDER BY r.createdAt DESC")
  List<Report> findAllExceptDeleted(@Param("deletedState") ReportState deletedState);

  @Query(
      "SELECT r FROM Report r WHERE "
          + "r.requester = :user AND "
          + "r.reportState != :deletedState "
          + "ORDER BY r.createdAt DESC")
  List<Report> findAllByUserExceptDeleted(
      @Param("user") User user, @Param("deletedState") ReportState deletedState);

  long countByReportState(ReportState reportState);

  @Query("SELECT r FROM Report r WHERE " + "r.id = :id AND " + "r.reportState != :deletedState")
  Optional<Report> findByIdExceptDeleted(
      @Param("id") Long id, @Param("deletedState") ReportState deletedState);

  @Query(
      "SELECT r FROM Report r WHERE "
          + "r.id = :id AND "
          + "r.requester = :user AND "
          + "r.reportState != :deletedState")
  Optional<Report> findByIdAndUserExceptDeleted(
      @Param("id") Long id,
      @Param("user") User user,
      @Param("deletedState") ReportState deletedState);
}
