package com.monkcommerce.model;

import lombok.Data;

import java.time.LocalDate;
@Data
public class CouponUpdateDTO {

    private String type;
    private Double discount;
    private String condition;
    private Integer repetitionLimit;
    private LocalDate startDate;
    private LocalDate endDate;
}
