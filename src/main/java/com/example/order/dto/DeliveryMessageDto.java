package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryMessageDto {

    private UUID orderId;

    private String city;

    private String street;

    private String buildingNumber;

    private String contactPhoneNumber;
}
