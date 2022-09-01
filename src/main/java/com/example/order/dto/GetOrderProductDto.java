package com.example.order.dto;

import com.example.order.model.MeasureUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class GetOrderProductDto {

    private Long id;

    private UUID productId;

    private Integer amount;

    private MeasureUnit measureUnit;

    private BigDecimal price;
}
