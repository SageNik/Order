package com.example.order.config;

import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import com.example.order.statemachine.action.CreditCardAuthorizationAction;
import com.example.order.statemachine.action.ErrorAction;
import com.example.order.statemachine.action.OrderCancellationAction;
import com.example.order.statemachine.action.OrderDeliveryAction;
import com.example.order.statemachine.action.OrderManufactureAction;
import com.example.order.statemachine.action.OrderPaymentAction;
import com.example.order.statemachine.action.TicketCreationAction;
import com.example.order.statemachine.guard.OrderCancelGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static com.example.order.model.OrderState.APPROVAL_PENDING;
import static com.example.order.model.OrderState.CREATED;
import static com.example.order.model.OrderState.CREDIT_CARD_AUTHORIZATION_FAILED;
import static com.example.order.model.OrderState.CREDIT_CARD_AUTHORIZED;
import static com.example.order.model.OrderState.DELIVERED;
import static com.example.order.model.OrderState.DELIVERY_FAILED;
import static com.example.order.model.OrderState.MANUFACTURED;
import static com.example.order.model.OrderState.ORDER_CANCELED;
import static com.example.order.model.OrderState.ORDER_PAYED;
import static com.example.order.model.OrderState.ORDER_REJECTED;
import static com.example.order.model.OrderState.PAYMENT_FAILED;
import static com.example.order.model.OrderState.TICKET_CREATED;
import static com.example.order.model.OrderState.TICKET_CREATION_FAILED;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent>{

    @Autowired
    private CreditCardAuthorizationAction creditCardAuthorizationAction;
    @Autowired
    private ErrorAction errorAction;
    @Autowired
    private TicketCreationAction ticketCreationAction;
    @Autowired
    private OrderPaymentAction orderPaymentAction;
    @Autowired
    private OrderManufactureAction orderManufactureAction;
    @Autowired
    private OrderDeliveryAction orderDeliveryAction;
    @Autowired
    private OrderCancelGuard orderCancelGuard;
    @Autowired
    private OrderCancellationAction orderCancellationAction;

    @Override
    public void configure(final StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(false)
                .listener(listener());
    }

    @Override
    public void configure(final StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(CREATED)
                .end(DELIVERED)
                .end(ORDER_CANCELED)
                .end(ORDER_REJECTED)
                .states(EnumSet.allOf(OrderState.class));
    }

    @Override
    public void configure(final StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .target(APPROVAL_PENDING)
                .event(OrderEvent.ORDER_CREATED)
                .action(creditCardAuthorizationAction, errorAction)

                .and()
                .withExternal()
                .source(APPROVAL_PENDING)
                .target(CREDIT_CARD_AUTHORIZED)
                .event(OrderEvent.CREDIT_CARD_AUTHORIZED)
                .action(ticketCreationAction, errorAction)

                .and()
                .withExternal()
                .source(APPROVAL_PENDING)
                .target(CREDIT_CARD_AUTHORIZATION_FAILED)
                .event(OrderEvent.CREDIT_CARD_AUTHORIZATION_FAILED)
                .action(errorAction)

                .and()
                .withExternal()
                .source(CREDIT_CARD_AUTHORIZED)
                .target(TICKET_CREATED)
                .event(OrderEvent.TICKET_CREATED)
                .action(orderPaymentAction, errorAction)

                .and()
                .withExternal()
                .source(CREDIT_CARD_AUTHORIZED)
                .target(TICKET_CREATION_FAILED)
                .event(OrderEvent.TICKET_CREATION_FAILED)
                .action(errorAction)

                .and()
                .withExternal()
                .source(TICKET_CREATED)
                .target(ORDER_PAYED)
                .event(OrderEvent.CREDIT_CARD_CHARGED)
                .action(orderManufactureAction, errorAction)

                .and()
                .withExternal()
                .source(TICKET_CREATED)
                .target(PAYMENT_FAILED)
                .event(OrderEvent.PAYMENT_FAILED)
                .action(errorAction)

                .and()
                .withExternal()
                .source(ORDER_PAYED)
                .target(MANUFACTURED)
                .event(OrderEvent.MANUFACTURED)
                .action(orderDeliveryAction, errorAction)

                .and()
                .withExternal()
                .source(MANUFACTURED)
                .target(DELIVERED)
                .event(OrderEvent.DELIVERED)
//                .action(orderCompletedAction, errorAction)

                .and()
                .withExternal()
                .source(MANUFACTURED)
                .target(DELIVERY_FAILED)
                .event(OrderEvent.DELIVERY_FAILED)
                .action(errorAction)

                .and()
                .withExternal()
                .target(ORDER_CANCELED)
                .event(OrderEvent.ORDER_CANCELED)
                .guard(orderCancelGuard)
                .action(orderCancellationAction, errorAction);
    }

    @Bean
    public StateMachineListener<OrderState, OrderEvent> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }
}
