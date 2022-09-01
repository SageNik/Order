package com.example.order.controller;

import com.example.order.dto.GetOrderDto;
import com.example.order.dto.OrderDto;
import com.example.order.model.OrderState;
import com.example.order.model.OrderStatus;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createOrder(@RequestBody OrderDto orderDto){
        orderService.createOrderSaga(orderDto);
    }

    @GetMapping("/{id}")
    public GetOrderDto getOrder(@PathVariable String id){
        return orderService.getById(id);
    }

    @PostMapping("/status/update")
    public String updateStatus(@RequestParam UUID orderId, @RequestParam OrderState orderState){
        return orderService.updateOrderState(orderId, orderState);
    }

    @PostMapping("/cancel")
    public void updateStatus(@RequestParam UUID orderId){
         orderService.cancelOrder(orderId);
    }
}
