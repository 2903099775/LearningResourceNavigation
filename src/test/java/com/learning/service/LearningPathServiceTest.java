package com.learning.service;

import com.learning.common.ResponseResult;
import com.learning.entity.LearningPath;
import com.learning.entity.LearningUnit;
import com.learning.entity.LearningProgress;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.LearningUnitMapper;
import com.learning.mapper.LearningProgressMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.impl.LearningPathServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class LearningPathServiceTest {

    @Mock
    private LearningPathMapper learningPathMapper;

    @Mock
    private LearningUnitMapper learningUnitMapper;

    @Mock
    private LearningProgressMapper learningProgressMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private LearningPathServiceImpl learningPathService;

    public LearningPathServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPaths() {
        List<LearningPath> paths = new ArrayList<>();
        LearningPath path = new LearningPath();
        path.setTitle("Test Path");
        paths.add(path);

        when(learningPathMapper.findAll()).thenReturn(paths);

        ResponseResult result = learningPathService.getPaths();
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetPathById() {
        LearningPath path = new LearningPath();
        path.setId(1L);
        path.setTitle("Test Path");

        when(learningPathMapper.findById(1L)).thenReturn(path);

        ResponseResult result = learningPathService.getPathById(1L);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetPathByIdNotFound() {
        when(learningPathMapper.findById(1L)).thenReturn(null);

        ResponseResult result = learningPathService.getPathById(1L);
        assertEquals("路线不存在", result.getMessage());
    }

    @Test
    public void testGetPathStages() {
        LearningPath path = new LearningPath();
        path.setId(1L);
        path.setTitle("Test Path");

        List<LearningUnit> units = new ArrayList<>();
        LearningUnit unit = new LearningUnit();
        unit.setTitle("Test Unit");
        units.add(unit);

        when(learningPathMapper.findById(1L)).thenReturn(path);
        when(learningUnitMapper.findByPathId(1L)).thenReturn(units);

        ResponseResult result = learningPathService.getPathStages(1L);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetPathStagesPathNotFound() {
        when(learningPathMapper.findById(1L)).thenReturn(null);

        ResponseResult result = learningPathService.getPathStages(1L);
        assertEquals("路线不存在", result.getMessage());
    }

    @Test
    public void testGetPathProgress() {
        LearningPath path = new LearningPath();
        path.setId(1L);
        path.setTitle("Test Path");

        List<LearningUnit> units = new ArrayList<>();
        LearningUnit unit1 = new LearningUnit();
        unit1.setId(1L);
        unit1.setTitle("Test Unit 1");
        units.add(unit1);

        LearningUnit unit2 = new LearningUnit();
        unit2.setId(2L);
        unit2.setTitle("Test Unit 2");
        units.add(unit2);

        List<LearningProgress> progressList = new ArrayList<>();
        LearningProgress progress1 = new LearningProgress();
        progress1.setUnitId(1L);
        progress1.setStatus("COMPLETED");
        progressList.add(progress1);

        when(learningPathMapper.findById(1L)).thenReturn(path);
        when(learningUnitMapper.findByPathId(1L)).thenReturn(units);
        when(learningProgressMapper.findByUserAndPath(1L, 1L)).thenReturn(progressList);

        ResponseResult result = learningPathService.getPathProgress(1L);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetPathProgressPathNotFound() {
        when(learningPathMapper.findById(1L)).thenReturn(null);

        ResponseResult result = learningPathService.getPathProgress(1L);
        assertEquals("路线不存在", result.getMessage());
    }
}
