package com.learning.service.admin;

import com.learning.common.ResponseResult;
import com.learning.service.impl.admin.AdminStatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminStatsServiceTest {

    @Autowired
    private AdminStatsService adminStatsService;

    @Test
    public void testGetStats() {
        ResponseResult result = adminStatsService.getStats();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetUserStats() {
        ResponseResult result = adminStatsService.getUserStats();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetPathStats() {
        ResponseResult result = adminStatsService.getPathStats();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetUnitStats() {
        ResponseResult result = adminStatsService.getUnitStats();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetWeeklyReport() {
        ResponseResult result = adminStatsService.getWeeklyReport();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetPieChartData() {
        ResponseResult result = adminStatsService.getPieChartData();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetBarChartData() {
        ResponseResult result = adminStatsService.getBarChartData();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetLineChartData() {
        ResponseResult result = adminStatsService.getLineChartData();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
}
