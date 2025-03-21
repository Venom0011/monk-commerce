package com.monkcommerce.repository;

import com.monkcommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findByStartDateBeforeAndEndDateAfter(LocalDate currentDate, LocalDate currentDate2);
}
