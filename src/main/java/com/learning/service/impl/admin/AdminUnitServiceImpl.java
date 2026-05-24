package com.learning.service.impl.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningUnit;
import com.learning.mapper.LearningUnitMapper;
import com.learning.service.admin.AdminUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUnitServiceImpl implements AdminUnitService {

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Override
    public ResponseResult getUnitList(Integer page, Integer size, String keyword, String type, String platform) {
        int offset = (page - 1) * size;

        List<LearningUnit> units = learningUnitMapper.selectList(offset, size, keyword, type, platform);
        int total = learningUnitMapper.countWithFilters(keyword, type, platform);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", units);

        return ResponseResult.success(result);
    }

    @Override
    public ResponseResult getUnitById(Long id) {
        LearningUnit unit = learningUnitMapper.findById(id);
        if (unit == null) {
            return ResponseResult.error("学习单元不存在");
        }
        return ResponseResult.success(unit);
    }

    @Override
    @Transactional
    public ResponseResult createUnit(LearningUnit unit) {
        if (unit.getTitle() == null || unit.getTitle().trim().isEmpty()) {
            return ResponseResult.error("单元名称不能为空");
        }
        if (unit.getStageId() == null) {
            return ResponseResult.error("请选择所属小节");
        }

        unit.setCreatedAt(LocalDateTime.now());
        unit.setStatus(1);
        if (unit.getViewCount() == null) {
            unit.setViewCount(0);
        }
        if (unit.getSortOrder() == null) {
            unit.setSortOrder(0);
        }
        if (unit.getIsVipOnly() == null) {
            unit.setIsVipOnly(0);
        }

        learningUnitMapper.insert(unit);
        return ResponseResult.success("学习单元创建成功", unit);
    }

    @Override
    @Transactional
    public ResponseResult updateUnit(LearningUnit unit) {
        LearningUnit existingUnit = learningUnitMapper.findById(unit.getId());
        if (existingUnit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        // 处理可能为null的字段，使用现有值或默认值
        if (unit.getStageId() == null) {
            unit.setStageId(existingUnit.getStageId());
        }
        if (unit.getSortOrder() == null) {
            unit.setSortOrder(existingUnit.getSortOrder());
        }
        if (unit.getStatus() == null) {
            unit.setStatus(existingUnit.getStatus());
        }
        if (unit.getIsVipOnly() == null) {
            unit.setIsVipOnly(existingUnit.getIsVipOnly());
        }
        if (unit.getContentType() == null) {
            unit.setContentType(existingUnit.getContentType());
        }
        if (unit.getViewCount() == null) {
            unit.setViewCount(existingUnit.getViewCount() != null ? existingUnit.getViewCount() : 0);
        }

        learningUnitMapper.update(unit);
        return ResponseResult.success("学习单元更新成功", unit);
    }

    @Override
    @Transactional
    public ResponseResult deleteUnit(Long id) {
        LearningUnit existingUnit = learningUnitMapper.findById(id);
        if (existingUnit == null) {
            return ResponseResult.error("学习单元不存在");
        }

        learningUnitMapper.delete(id);
        return ResponseResult.success("学习单元删除成功");
    }
}
