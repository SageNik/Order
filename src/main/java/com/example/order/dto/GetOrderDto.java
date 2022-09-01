package com.example.order.dto;

import com.example.order.model.DeliveryAddress;
import com.example.order.model.OrderState;
import com.example.order.model.OrderStatus;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class GetOrderDto {

    private UUID id;

    private OrderState state;

    private DeliveryAddress address;

    private Set<GetOrderProductDto> products;
}
