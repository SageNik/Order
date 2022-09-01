package com.example.order.service;

import com.example.order.dto.AccountMessageDto;
import com.example.order.dto.AccountReplyMessageDto;
import com.example.order.dto.AccountReplyStatus;
import com.example.order.dto.DeliveryMessageDto;
import com.example.order.dto.DeliveryReplyMessageDto;
import com.example.order.dto.DeliveryReplyStatus;
import com.example.order.dto.ManufactureMessageDto;
import com.example.order.dto.ManufactureReplyMessageDto;
import com.example.order.dto.ManufactureReplyStatus;
import com.example.order.model.Order;
import com.example.order.model.OrderState;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.example.order.controller.RabbitMqConfig.ACCOUNT_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.ACCOUNT_REPLY_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.ACCOUNT_RETURN_PAYMENT_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.CANCELLATION_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.DELIVERY_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.DELIVERY_REPLY_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.MANUFACTURE_CANCEL_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.MANUFACTURE_MESSAGE_QUEUE;
import static com.example.order.controller.RabbitMqConfig.MANUFACTURE_REPLY_MESSAGE_QUEUE;
import static com.example.order.model.OrderEvent.CREDIT_CARD_AUTHORIZATION_FAILED;
import static com.example.order.model.OrderEvent.CREDIT_CARD_AUTHORIZED;
import static com.example.order.model.OrderEvent.CREDIT_CARD_CHARGED;
import static com.example.order.model.OrderEvent.DELIVERED;
import static com.example.order.model.OrderEvent.DELIVERY_FAILED;
import static com.example.order.model.OrderEvent.MANUFACTURED;
import static com.example.order.model.OrderEvent.PAYMENT_FAILED;
import static com.example.order.model.OrderEvent.TICKET_CREATED;
import static com.example.order.model.OrderEvent.TICKET_CREATION_FAILED;
import static com.example.order.utils.JsonConverterUtil.deserialize;
import static com.example.order.utils.JsonConverterUtil.serialize;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final StateMachineHandlerService stateMachineHandlerService;

    private final RabbitTemplate rabbitTemplate;

    private final OrderRepository orderRepository;

    @RabbitListener(queues = ACCOUNT_REPLY_MESSAGE_QUEUE)
    public void receiveMessageFromAccountService(String message) {
        final AccountReplyMessageDto reply = deserialize(message, AccountReplyMessageDto.class);
        log.info("Getting reply from account service for type:" + reply.getStatus());
        switch (Objects.requireNonNull(reply).getStatus()) {
            case AUTHORIZED -> stateMachineHandlerService.sendStateMachineEvent(CREDIT_CARD_AUTHORIZED, reply.getOrderId());
            case PAID -> stateMachineHandlerService.sendStateMachineEvent(CREDIT_CARD_CHARGED, reply.getOrderId());
            case CREDIT_CARD_AUTHORIZATION_FAILED -> stateMachineHandlerService.sendStateMachineEvent(CREDIT_CARD_AUTHORIZATION_FAILED, reply.getOrderId());
            case PAYMENT_FAILED -> stateMachineHandlerService.sendStateMachineEvent(PAYMENT_FAILED, reply.getOrderId());
            default -> throw new IllegalArgumentException("Invalid account reply message status");
        }
    }

    @RabbitListener(queues = MANUFACTURE_REPLY_MESSAGE_QUEUE)
    public void receiveMessageFromManufactureService(String message) {
        final ManufactureReplyMessageDto reply = deserialize(message, ManufactureReplyMessageDto.class);
        log.info("Getting reply from manufacture service for status:" + reply.getStatus());
        switch (Objects.requireNonNull(reply).getStatus()) {
            case TICKET_CREATED -> stateMachineHandlerService.sendStateMachineEvent(TICKET_CREATED, reply.getOrderId());
            case MANUFACTURED -> stateMachineHandlerService.sendStateMachineEvent(MANUFACTURED, reply.getOrderId());
            case TICKET_CREATION_FAILED -> stateMachineHandlerService.sendStateMachineEvent(TICKET_CREATION_FAILED, reply.getOrderId());
            default -> throw new IllegalArgumentException("Invalid manufacture reply message status");

        }
    }

    @RabbitListener(queues = DELIVERY_REPLY_MESSAGE_QUEUE)
    public void receiveMessageFromDeliveryService(String message) {
        final DeliveryReplyMessageDto reply = deserialize(message, DeliveryReplyMessageDto.class);
        log.info("Getting reply from delivery service for status:" + reply.getStatus());
        switch (Objects.requireNonNull(reply).getStatus()) {
            case DELIVERED -> stateMachineHandlerService.sendStateMachineEvent(DELIVERED, reply.getOrderId());
            case DELIVERY_FAILED -> stateMachineHandlerService.sendStateMachineEvent(DELIVERY_FAILED, reply.getOrderId());
            default -> throw new IllegalArgumentException("Invalid delivery reply message status");

        }
    }

    @RabbitListener(queues = CANCELLATION_MESSAGE_QUEUE)
    public void receiveMessageForOrderCancellation(String message) {
        log.info("Cancellation for order with id: " + message);
        UUID orderId = UUID.fromString(message);
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order not found by id: %s", orderId)));
        switch (order.getState()){
            case CREATED, CREDIT_CARD_AUTHORIZED, TICKET_CREATION_FAILED -> changeStateToCancel(order);
            case TICKET_CREATED, PAYMENT_FAILED -> {
                changeStateToCancel(order);
                //TODO send command to Manufacture service for cancelling created ticket
                rabbitTemplate.convertAndSend(MANUFACTURE_CANCEL_MESSAGE_QUEUE, orderId);
            }
            case ORDER_PAYED, MANUFACTURED, DELIVERY_FAILED -> {
                changeStateToCancel(order);
                //TODO send command to Account service for returning money for order
                rabbitTemplate.convertAndSend(ACCOUNT_RETURN_PAYMENT_MESSAGE_QUEUE, orderId);
                //TODO send command to Manufacture service for cancelling created ticket
                rabbitTemplate.convertAndSend(MANUFACTURE_CANCEL_MESSAGE_QUEUE, orderId);
            }
            default -> throw new IllegalArgumentException(
                    String.format("Illegal state for order cancellation! State: %s, orderId: %s", order.getState(), orderId));
        }
    }

    private void changeStateToCancel(Order order) {
        order.setState(OrderState.ORDER_CANCELED);
        orderRepository.save(order);
    }

    @SneakyThrows
    @RabbitListener(queues = ACCOUNT_MESSAGE_QUEUE)
    public void mockMessageFromAccountService(String message) {
        final AccountMessageDto mes = deserialize(message, AccountMessageDto.class);
        log.info("Mock for getting reply from account service for type:" + mes.getMessageType());
        Thread.sleep(1000);
        switch (Objects.requireNonNull(mes).getMessageType()) {
            case AUTHORIZATION -> rabbitTemplate.convertAndSend(ACCOUNT_REPLY_MESSAGE_QUEUE, serialize(new AccountReplyMessageDto(mes.getOrderId(), AccountReplyStatus.AUTHORIZED)));
            case PAYMENT -> rabbitTemplate.convertAndSend(ACCOUNT_REPLY_MESSAGE_QUEUE, serialize(new AccountReplyMessageDto(mes.getOrderId(), AccountReplyStatus.PAID)));
            case AUTHORIZATION_FAIL -> rabbitTemplate.convertAndSend(ACCOUNT_REPLY_MESSAGE_QUEUE, serialize(new AccountReplyMessageDto(mes.getOrderId(), AccountReplyStatus.CREDIT_CARD_AUTHORIZATION_FAILED)));
            case PAYMENT_FAIL -> rabbitTemplate.convertAndSend(ACCOUNT_REPLY_MESSAGE_QUEUE, serialize(new AccountReplyMessageDto(mes.getOrderId(), AccountReplyStatus.PAYMENT_FAILED)));
            default -> throw new IllegalArgumentException("Invalid account reply message type");
        }
    }

    @SneakyThrows
    @RabbitListener(queues = MANUFACTURE_MESSAGE_QUEUE)
    public void mockMessageFromManufactureService(String message) {
        final ManufactureMessageDto mes = deserialize(message, ManufactureMessageDto.class);
        log.info("Mock for getting reply from manufacture service for type:" + mes.getMessageType());
        Thread.sleep(1000);
        switch (Objects.requireNonNull(mes).getMessageType()) {
            case TICKET_CREATION -> rabbitTemplate.convertAndSend(MANUFACTURE_REPLY_MESSAGE_QUEUE, serialize(new ManufactureReplyMessageDto(mes.getOrderId(), ManufactureReplyStatus.TICKET_CREATED)));
            case ORDER_PRODUCT_MANUFACTURE -> rabbitTemplate.convertAndSend(MANUFACTURE_REPLY_MESSAGE_QUEUE, serialize(new ManufactureReplyMessageDto(mes.getOrderId(), ManufactureReplyStatus.MANUFACTURED)));
            case TICKET_CREATION_FAIL -> rabbitTemplate.convertAndSend(MANUFACTURE_REPLY_MESSAGE_QUEUE, serialize(new ManufactureReplyMessageDto(mes.getOrderId(), ManufactureReplyStatus.TICKET_CREATION_FAILED)));
            default -> throw new IllegalArgumentException("Invalid manufacture reply message type");
        }
    }

    @SneakyThrows
    @RabbitListener(queues = DELIVERY_MESSAGE_QUEUE)
    public void mockMessageFromDeliveryService(String message) {
        final DeliveryMessageDto mes = deserialize(message, DeliveryMessageDto.class);
        log.info("Mock for getting reply from delivery service");
        Thread.sleep(1000);
        try{
            rabbitTemplate.convertAndSend(DELIVERY_REPLY_MESSAGE_QUEUE, serialize(new DeliveryReplyMessageDto(mes.getOrderId(), DeliveryReplyStatus.DELIVERED)));
        }catch (Exception ex){
            rabbitTemplate.convertAndSend(DELIVERY_REPLY_MESSAGE_QUEUE, serialize(new DeliveryReplyMessageDto(mes.getOrderId(), DeliveryReplyStatus.DELIVERY_FAILED)));
            ex.printStackTrace();
        }
    }
}
