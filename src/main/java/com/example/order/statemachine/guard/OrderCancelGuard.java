package com.example.order.statemachine.guard;

import com.example.order.model.Order;
import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import static com.example.order.service.OrderService.ORDER_VARIABLE;
import static com.example.order.utils.JsonConverterUtil.deserialize;

@Component
public class OrderCancelGuard implements Guard<OrderState, OrderEvent> {

    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> stateContext) {
        Order order = deserialize(stateContext.getExtendedState().getVariables().get(ORDER_VARIABLE).toString(), Order.class);
        return !OrderState.DELIVERED.equals(order.getState()) && !OrderState.ORDER_REJECTED.equals(order.getState())
                && !OrderState.ORDER_CANCELED.equals(order.getState());
    }
}
