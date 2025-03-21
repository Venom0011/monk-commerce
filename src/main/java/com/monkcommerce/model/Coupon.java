package com.monkcommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;
    private Double discount;
    private String condition;
    private Integer repetitionLimit;
    private LocalDate startDate;
    private LocalDate endDate;

    // BxGy specific fields
    @ElementCollection
    private List<String> buyProducts=new ArrayList<>();  // List of product names or IDs in the "buy" set

    @ElementCollection
    private List<String> getProducts=new ArrayList<>();  // List of product names or IDs in the "get" set

}
