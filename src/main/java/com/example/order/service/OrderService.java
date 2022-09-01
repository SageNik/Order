package com.example.order.service;

import com.example.order.dto.GetOrderDto;
import com.example.order.dto.OrderDto;
import com.example.order.exception.IllegalOrderStatusException;
import com.example.order.model.Order;
import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.order.mapper.OrderMapper.ORDER_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final StateMachineHandlerService stateMachineHandlerService;

    public static final String ORDER_VARIABLE = "order";

    @Transactional
    public void createOrderSaga(OrderDto orderDto) {
        final Order order = ORDER_MAPPER.mapToOrder(orderDto);
        order.setState(OrderState.CREATED);
        order.getProducts().forEach(orderProduct -> orderProduct.setOrder(order));
        Order savedOrder = orderRepository.save(order);
        stateMachineHandlerService.sendStateMachineEvent(OrderEvent.ORDER_CREATED, savedOrder.getId());
    }

    @Transactional(readOnly = true)
    public GetOrderDto getById(String orderId) {
        final UUID uuid = UUID.fromString(orderId);
        return orderRepository.findById(uuid)
                .map(ORDER_MAPPER::mapToGetOrderDto)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order not found by id: %s", orderId)));
    }

    @Transactional
    public String updateOrderState(UUID orderId, OrderState newOrderState) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order not found by id: %s", orderId)));
        changeOrderStatus(order, newOrderState);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getState().name();
    }

    private void changeOrderStatus(Order order, OrderState newOrderState) {
        if (OrderState.CREATED.equals(order.getState())) {
            order.setState(newOrderState);
            addStatusChangeLog(order, newOrderState);
        } else throwStatusException(order, newOrderState);
    }

    private void throwStatusException(Order order, OrderState newOrderState) {
        String msg = String.format("Invalid current state: %s for changing to: %s", order.getState(), newOrderState);
        log.warn(msg);
        throw new IllegalOrderStatusException(msg);
    }

    private void addStatusChangeLog(Order order, OrderState newOrderState) {
        log.info(String.format("State for order: %s has been changed from : %s -> to %s", order.getId(), order.getState(), newOrderState));
    }

    public void rejectOrder(UUID orderId) {
        //TODO add logic for rollback or/and compensate transaction
        updateOrderState(orderId, OrderState.ORDER_REJECTED);
    }

    public void cancelOrder(UUID orderId) {
        stateMachineHandlerService.sendStateMachineEvent(OrderEvent.ORDER_CANCELED, orderId);
    }
}
