package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountMessageDto {

    private UUID orderId;

    private Long clientId;

    private BigDecimal totalPrice;

    private AccountMessageType messageType;
}
