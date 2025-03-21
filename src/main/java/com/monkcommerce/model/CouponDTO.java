package com.monkcommerce.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CouponDTO {

    private String type;
    private Double discount;
    private String condition;
    private Integer repetitionLimit;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<String> buyProducts;
    private List<String> getProducts;
}

