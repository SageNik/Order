package com.example.order.mapper;

import com.example.order.dto.AccountMessageType;
import com.example.order.dto.DeliveryMessageDto;
import com.example.order.dto.GetOrderDto;
import com.example.order.dto.GetOrderProductDto;
import com.example.order.dto.ManufactureMessageDto;
import com.example.order.dto.ManufactureMessageType;
import com.example.order.dto.ManufactureProductMessageDto;
import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderProductDto;
import com.example.order.dto.AccountMessageDto;
import com.example.order.model.Order;
import com.example.order.model.OrderProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    public static final OrderMapper ORDER_MAPPER = Mappers.getMapper(OrderMapper.class);

    public abstract GetOrderDto mapToGetOrderDto(Order order);

    public abstract GetOrderProductDto mapToGetOrderProductDto(OrderProduct orderProduct);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    public abstract Order mapToOrder(OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    public abstract OrderProduct mapToOrderProduct(OrderProductDto orderProductDto);

    public abstract Set<OrderProduct> mapToOrderProducts(Set<OrderProductDto> productDtos);

    public abstract Set<GetOrderProductDto> mapToOrderProductDtos(Set<OrderProduct> orderProducts);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "messageType", source = "messageType")
    public abstract AccountMessageDto mapToAccountMessageDto(Order order, BigDecimal totalPrice, AccountMessageType messageType);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "messageType", source = "messageType")
    @Mapping(target = "products", source = "order.products")
    public abstract ManufactureMessageDto mapToManufactureMessageDto(Order order, ManufactureMessageType messageType);

    public abstract ManufactureProductMessageDto mapToProductsMessageDto (OrderProduct orderProduct);

    public abstract Set<ManufactureProductMessageDto> mapToProductsMessageDtos(Set<OrderProduct> orderProducts);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "buildingNumber", source = "address.buildingNumber")
    @Mapping(target = "contactPhoneNumber", source = "address.contactPhoneNumber")
    public abstract DeliveryMessageDto mapToDeliveryMessageDto(Order order);
}
