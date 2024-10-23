package com.khanh.timekeeping.repositories;


import com.khanh.timekeeping.entities.Attendance;
import com.khanh.timekeeping.entities.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findFirstByUserIdAndCheckInTimeBetween(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(
            value =
                    """
                             SELECT a.*
                             FROM attendances a
                             WHERE a.user_id = :userId
                               AND a.created_at <= :dateTime
                             ORDER BY a.created_at DESC
                             LIMIT :limit
                             """,
            nativeQuery = true)
    List<Attendance> findHistoryAttendances(Long userId, LocalDateTime dateTime, Integer limit);

    Long countByUserIdAndCheckInTimeBetweenAndStatus(
            Long userId, LocalDateTime startTime, LocalDateTime endTime, AttendanceStatus status);
}
