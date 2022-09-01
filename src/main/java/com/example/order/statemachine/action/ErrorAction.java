package com.example.order.statemachine.action;

import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.order.controller.RabbitMqConfig.CANCELLATION_MESSAGE_QUEUE;
import static com.example.order.service.OrderService.ORDER_VARIABLE;
import static com.example.order.service.StateMachineHandlerService.ORDER_ID_HEADER;

@Component
public class ErrorAction implements Action<OrderState, OrderEvent> {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        final UUID orderId = UUID.fromString(context.getMessage().getHeaders().get(ORDER_ID_HEADER).toString());
        rabbitTemplate.convertAndSend(CANCELLATION_MESSAGE_QUEUE, orderId);
    }
}
