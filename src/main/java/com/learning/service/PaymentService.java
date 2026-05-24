package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.Payment;

/**
 * 支付服务接口
 * 定义支付相关的方法，包括创建订单、支付回调和获取订单列表等
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Payment
 */
public interface PaymentService {
    ResponseResult createOrder(Payment payment);
    
    ResponseResult paymentNotify(Payment payment);
    
    ResponseResult getOrders();
}