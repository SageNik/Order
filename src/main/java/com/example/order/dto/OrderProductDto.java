package com.example.order.dto;

import com.example.order.model.MeasureUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDto {

    private UUID productId;

    private Integer amount;

    private MeasureUnit measureUnit;

    private BigDecimal price;
}
