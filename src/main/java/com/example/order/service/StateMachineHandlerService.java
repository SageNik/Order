package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.model.OrderEvent;
import com.example.order.model.OrderState;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.example.order.utils.JsonConverterUtil.serialize;

@Service
@RequiredArgsConstructor
public class StateMachineHandlerService {

    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    private final OrderRepository orderRepository;

    public final static String ORDER_ID_HEADER = "orderId";

    private final String ORDER = "order";

    public StateMachine<OrderState, OrderEvent> getStateMachine(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException(String.format("Order not found with id: %s", orderId)));

        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(orderId);
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {

                    sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<OrderState, OrderEvent>() {

                        @Override
                        public void preStateChange(State<OrderState, OrderEvent> state, Message<OrderEvent> message,
                                                   Transition<OrderState, OrderEvent> transition, StateMachine<OrderState,
                                OrderEvent> stateMachine, StateMachine<OrderState,
                                OrderEvent> rootStateMachine) {

                            Optional.ofNullable(message).ifPresent(msg -> {

                                Optional.ofNullable((UUID) msg.getHeaders().getOrDefault(ORDER_ID_HEADER, ""))
                                        .ifPresent(orderId1 -> {
                                            Order order1 = orderRepository.findById(orderId)
                                                    .orElseThrow(()-> new IllegalArgumentException(String.format("Order not found with id: %s", orderId)));
                                            order1.setState(state.getId());
                                            orderRepository.save(order1);
                                        });
                            });

                        }
                    });
                    sma.resetStateMachine(new DefaultStateMachineContext<>(order.getState(), null, null, null));
                });
        sm.getExtendedState().getVariables().put(ORDER, serialize(order));
        sm.start();
        return sm;
    }

    public void sendStateMachineEvent(OrderEvent event, UUID orderId){
        final StateMachine<OrderState, OrderEvent> stateMachine = getStateMachine(orderId);
        Message<OrderEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();
        stateMachine.sendEvent(message);
    }
}
