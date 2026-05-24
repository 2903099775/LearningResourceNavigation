package com.learning.mapper;

import com.learning.entity.RefundRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 退款申请Mapper接口
 */
@Mapper
public interface RefundRequestMapper {

    /**
     * 插入退款申请
     */
    @Insert("INSERT INTO refund_requests (user_id, order_no, reason, status, created_at) " +
            "VALUES (#{userId}, #{orderNo}, #{reason}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RefundRequest request);

    /**
     * 根据ID查询退款申请
     */
    @Select("SELECT * FROM refund_requests WHERE id = #{id}")
    RefundRequest findById(Long id);

    /**
     * 根据用户ID查询退款申请列表
     */
    @Select("SELECT * FROM refund_requests WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<RefundRequest> findByUserId(Long userId);

    /**
     * 查询所有待审核的退款申请
     */
    @Select("SELECT * FROM refund_requests WHERE status = 'PENDING' ORDER BY created_at DESC")
    List<RefundRequest> findPendingRequests();

    /**
     * 查询所有退款申请
     */
    @Select("SELECT * FROM refund_requests ORDER BY created_at DESC")
    List<RefundRequest> findAll();

    /**
     * 更新退款申请状态
     */
    @Update("UPDATE refund_requests SET status = #{status}, admin_remark = #{adminRemark}, " +
            "processed_at = #{processedAt} WHERE id = #{id}")
    int updateStatus(RefundRequest request);

    /**
     * 删除退款申请
     */
    @Delete("DELETE FROM refund_requests WHERE id = #{id}")
    int delete(Long id);

    /**
     * 统计待审核的退款申请数量
     */
    @Select("SELECT COUNT(*) FROM refund_requests WHERE status = 'PENDING'")
    int countPending();
}