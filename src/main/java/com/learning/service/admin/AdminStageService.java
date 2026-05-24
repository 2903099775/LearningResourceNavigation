package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.PathStage;

import java.util.List;

/**
 * 管理员学习阶段服务接口
 * 定义管理员对学习阶段的创建、更新、删除和查询等操作方法
 */
public interface AdminStageService {
    ResponseResult getStagesByPathId(Long pathId);

    ResponseResult getStageById(Long id);

    ResponseResult createStage(PathStage stage);

    ResponseResult updateStage(PathStage stage);

    ResponseResult deleteStage(Long id);

    ResponseResult batchSaveStages(Long pathId, List<PathStage> stages);
}
