package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.entity.Payment;
import com.learning.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * 负责处理支付相关的API请求，包括创建订单、支付回调和获取订单列表等操作
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Payment, com.learning.service.PaymentService
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseResult<String> createOrder(@RequestBody Payment payment) {
        return paymentService.createOrder(payment);
    }

    @PostMapping("/notify")
    public ResponseResult<String> paymentNotify(@RequestBody Payment payment) {
        return paymentService.paymentNotify(payment);
    }

    @GetMapping("/orders")
    public ResponseResult<Object> getOrders() {
        return paymentService.getOrders();
    }
}