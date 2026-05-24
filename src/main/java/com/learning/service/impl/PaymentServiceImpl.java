package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Payment;
import com.learning.mapper.PaymentMapper;
import com.learning.mapper.UserMapper;
import com.learning.entity.User;
import com.learning.service.AchievementService;
import com.learning.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 支付服务实现类
 * 负责处理订单创建、支付回调和订单查询等支付相关功能
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AchievementService achievementService;

    @Override
    public ResponseResult createOrder(Payment payment) {
        log.info("创建订单请求: amount={}, months={}", payment.getAmount(), payment.getMonths());

        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("创建订单失败: 用户未登录");
                return ResponseResult.error("用户未登录");
            }

            String username = authentication.getName();
            User user = userMapper.findByUsername(username);
            if (user == null) {
                log.warn("创建订单失败: 用户不存在");
                return ResponseResult.error("用户不存在");
            }

            // 验证参数
            if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("创建订单失败: 金额无效");
                return ResponseResult.error("金额无效");
            }

            if (payment.getMonths() == null || payment.getMonths() <= 0) {
                log.warn("创建订单失败: 月份无效");
                return ResponseResult.error("月份无效");
            }

            // 生成订单号
            String orderNo = "ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            // 计算过期日期
            LocalDate expireDate = LocalDate.now().plusMonths(payment.getMonths());

            // 创建订单
            Payment newPayment = new Payment();
            newPayment.setUserId(user.getId());
            newPayment.setOrderNo(orderNo);
            newPayment.setAmount(payment.getAmount());
            newPayment.setMonths(payment.getMonths());
            newPayment.setStatus("PENDING");
            newPayment.setCreatedAt(LocalDateTime.now());
            newPayment.setExpireDate(expireDate);

            // 保存到数据库
            paymentMapper.insert(newPayment);

            log.info("订单创建成功: orderNo={}, userId={}", orderNo, user.getId());

            // 构建响应
            return ResponseResult.success("订单创建成功", newPayment);
        } catch (Exception e) {
            log.error("创建订单过程中发生异常: ", e);
            return ResponseResult.error("创建订单失败: 系统内部错误");
        }
    }

    @Override
    public ResponseResult paymentNotify(Payment payment) {
        log.info("支付回调请求: orderNo={}, status={}", payment.getOrderNo(), payment.getStatus());

        try {
            // 验证订单号
            if (payment.getOrderNo() == null || payment.getOrderNo().trim().isEmpty()) {
                log.warn("支付回调失败: 订单号为空");
                return ResponseResult.error("订单号为空");
            }

            // 查询订单
            Payment existingPayment = paymentMapper.findByOrderNo(payment.getOrderNo());
            if (existingPayment == null) {
                log.warn("支付回调失败: 订单不存在");
                return ResponseResult.error("订单不存在");
            }

            // 检查订单状态
            if (!"PENDING".equals(existingPayment.getStatus())) {
                log.warn("支付回调失败: 订单状态错误，当前状态={}", existingPayment.getStatus());
                return ResponseResult.error("订单状态错误");
            }

            // 更新订单状态
            existingPayment.setStatus("SUCCESS");
            existingPayment.setPayTime(LocalDateTime.now());
            paymentMapper.update(existingPayment);

            // 更新用户VIP过期日期
            User user = userMapper.findById(existingPayment.getUserId());
            if (user != null) {
                LocalDate newExpireDate;
                if (user.getVipExpireDate() != null && user.getVipExpireDate().isAfter(LocalDate.now())) {
                    // 如果用户当前是VIP，在现有过期日期基础上增加时长
                    newExpireDate = user.getVipExpireDate().plusMonths(existingPayment.getMonths());
                } else {
                    // 如果用户不是VIP或VIP已过期，从当前日期开始计算
                    newExpireDate = LocalDate.now().plusMonths(existingPayment.getMonths());
                }
                user.setVipExpireDate(newExpireDate);
                userMapper.update(user);
                log.info("用户VIP过期日期更新: userId={}, expireDate={}", user.getId(), newExpireDate);

                achievementService.checkAndUnlockAchievements(user.getId());
            }

            log.info("支付回调成功: orderNo={}", payment.getOrderNo());
            return ResponseResult.success("支付成功");
        } catch (Exception e) {
            log.error("支付回调过程中发生异常: ", e);
            return ResponseResult.error("支付回调失败: 系统内部错误");
        }
    }

    @Override
    public ResponseResult getOrders() {
        log.info("获取订单列表请求");

        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("获取订单列表失败: 用户未登录");
                return ResponseResult.error("用户未登录");
            }

            String username = authentication.getName();
            User user = userMapper.findByUsername(username);
            if (user == null) {
                log.warn("获取订单列表失败: 用户不存在");
                return ResponseResult.error("用户不存在");
            }

            // 查询用户的所有订单
            List<Payment> orders = paymentMapper.findByUserId(user.getId());

            log.info("获取订单列表成功: userId={}, orderCount={}", user.getId(), orders.size());
            return ResponseResult.success("获取订单列表成功", orders);
        } catch (Exception e) {
            log.error("获取订单列表过程中发生异常: ", e);
            return ResponseResult.error("获取订单列表失败: 系统内部错误");
        }
    }
}
