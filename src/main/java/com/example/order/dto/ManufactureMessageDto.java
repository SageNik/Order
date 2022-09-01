package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManufactureMessageDto {

    private UUID orderId;

    private ManufactureMessageType messageType;

   private Set<ManufactureProductMessageDto> products;
}
