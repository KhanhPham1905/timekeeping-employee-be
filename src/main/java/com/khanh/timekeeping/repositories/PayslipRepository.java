package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.entities.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    Payslip findFirstByUserIdAndPeriod(Long userId, Integer period);
}
