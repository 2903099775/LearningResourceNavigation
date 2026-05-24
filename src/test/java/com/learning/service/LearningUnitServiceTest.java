package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningUnit;
import com.learning.mapper.LearningUnitMapper;
import com.learning.service.impl.LearningUnitServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class LearningUnitServiceTest {

    @Mock
    private LearningUnitMapper learningUnitMapper;

    @InjectMocks
    private LearningUnitServiceImpl learningUnitService;

    public LearningUnitServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUnitById() {
        LearningUnit unit = new LearningUnit();
        unit.setId(1L);
        unit.setTitle("Test Unit");

        when(learningUnitMapper.findById(1L)).thenReturn(unit);

        ResponseResult result = learningUnitService.getUnitById(1L);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetUnitByIdNotFound() {
        when(learningUnitMapper.findById(1L)).thenReturn(null);

        ResponseResult result = learningUnitService.getUnitById(1L);
        assertEquals("学习单元不存在", result.getMessage());
    }
}
