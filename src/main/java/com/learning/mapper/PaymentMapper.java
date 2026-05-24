package com.learning.mapper;

import com.learning.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付数据访问接口
 * 负责支付相关的数据库操作，包括查询、插入和更新等
 * 引用文件：com.learning.entity.Payment
 */
@Mapper
public interface PaymentMapper {
    void insert(Payment payment);
    void update(Payment payment);
    Payment findById(@Param("id") Long id);
    List<Payment> findByUserId(@Param("userId") Long userId);
    List<Payment> findAll();
    Payment findByOrderNo(@Param("orderNo") String orderNo);
    Payment findLatestSuccessByUserId(@Param("userId") Long userId);
}
