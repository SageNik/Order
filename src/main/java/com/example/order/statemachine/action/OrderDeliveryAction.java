package com.example.order.statemachine.action;

import com.example.order.dto.DeliveryMessageDto;
import com.example.order.model.Order;
import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static com.example.order.controller.RabbitMqConfig.DELIVERY_MESSAGE_QUEUE;
import static com.example.order.mapper.OrderMapper.ORDER_MAPPER;
import static com.example.order.service.OrderService.ORDER_VARIABLE;
import static com.example.order.utils.JsonConverterUtil.deserialize;
import static com.example.order.utils.JsonConverterUtil.serialize;

@Component
public class OrderDeliveryAction implements Action<OrderState, OrderEvent> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        Order order = deserialize(context.getExtendedState().getVariables().get(ORDER_VARIABLE).toString(), Order.class);

        DeliveryMessageDto messageDto = ORDER_MAPPER.mapToDeliveryMessageDto(order);
        rabbitTemplate.convertAndSend(DELIVERY_MESSAGE_QUEUE, serialize(messageDto));
    }
}
