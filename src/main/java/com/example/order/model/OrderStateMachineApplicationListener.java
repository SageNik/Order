//package com.example.order.model;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.statemachine.StateContext;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.listener.StateMachineListener;
//import org.springframework.statemachine.state.State;
//import org.springframework.statemachine.transition.Transition;
//
//@Slf4j
//public class OrderStateMachineApplicationListener implements StateMachineListener<OrderState, OrderEvent> {
//    @Override
//    public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
//        if (from.getId() != null) {
//            log.info(String.format("Changing order state from: %s  to: %s", from.getId().name(), to.getId().name()));
//        }
//    }
//
//    @Override
//    public void stateEntered(State<OrderState, OrderEvent> state) {
//
//    }
//
//    @Override
//    public void stateExited(State<OrderState, OrderEvent> state) {
//
//    }
//
//    @Override
//    public void eventNotAccepted(Message<OrderEvent> message) {
//        log.info(String.format("The event: %s isn't accepted", message.getPayload().name()));
//    }
//
//    @Override
//    public void transition(Transition<OrderState, OrderEvent> transition) {
//
//    }
//
//    @Override
//    public void transitionStarted(Transition<OrderState, OrderEvent> transition) {
//
//    }
//
//    @Override
//    public void transitionEnded(Transition<OrderState, OrderEvent> transition) {
//
//    }
//
//    @Override
//    public void stateMachineStarted(StateMachine<OrderState, OrderEvent> stateMachine) {
//        log.info("Order state machine is started");
//    }
//
//    @Override
//    public void stateMachineStopped(StateMachine<OrderState, OrderEvent> stateMachine) {
//
//    }
//
//    @Override
//    public void stateMachineError(StateMachine<OrderState, OrderEvent> stateMachine, Exception e) {
//
//    }
//
//    @Override
//    public void extendedStateChanged(Object o, Object o1) {
//
//    }
//
//    @Override
//    public void stateContext(StateContext<OrderState, OrderEvent> stateContext) {
//
//    }
//}
