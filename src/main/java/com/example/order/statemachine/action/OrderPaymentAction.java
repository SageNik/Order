package com.example.order.statemachine.action;

import com.example.order.dto.AccountMessageDto;
import com.example.order.dto.AccountMessageType;
import com.example.order.model.Order;
import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

import static com.example.order.controller.RabbitMqConfig.ACCOUNT_MESSAGE_QUEUE;
import static com.example.order.mapper.OrderMapper.ORDER_MAPPER;
import static com.example.order.service.OrderService.ORDER_VARIABLE;
import static com.example.order.utils.JsonConverterUtil.deserialize;
import static com.example.order.utils.JsonConverterUtil.serialize;

@Component
public class OrderPaymentAction implements Action<OrderState, OrderEvent> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        Order order = deserialize(context.getExtendedState().getVariables().get(ORDER_VARIABLE).toString(), Order.class);
        BigDecimal totalPrice = Objects.requireNonNull(order).getProducts().stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AccountMessageDto messageDto = ORDER_MAPPER.mapToAccountMessageDto(order, totalPrice, AccountMessageType.PAYMENT);
        rabbitTemplate.convertAndSend(ACCOUNT_MESSAGE_QUEUE, serialize(messageDto));
    }
}
