package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 数据导入控制器
 * 提供数据导入功能的REST接口
 */
@RestController
@RequestMapping("/api/admin/import")
public class DataImportController {

    @Autowired
    private DataImportService dataImportService;

    /**
     * 导入学习路线数据
     */
    @PostMapping("/learning-paths")
    public ResponseResult importLearningPaths() {
        try {
            String filePath = "c:\\Users\\29030\\Documents\\trae_projects\\学习路线导航.md";
            dataImportService.importLearningPathsFromMd(filePath);
            return ResponseResult.success("学习路线数据导入成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(500, "文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error(500, "数据导入失败: " + e.getMessage());
        }
    }
}