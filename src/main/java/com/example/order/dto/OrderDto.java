package com.example.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class OrderDto {

    private Long clientId;

    private DeliveryAddressDTO address;

    private Set<OrderProductDto> products;

    private BigDecimal totalPrice;
}
