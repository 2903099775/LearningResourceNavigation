package com.learning.controller;

import com.learning.entity.Resource;
import com.learning.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习资源控制器
 * 负责处理学习资源相关的API请求，包括获取、创建、更新和删除等操作
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<Resource>> getResourcesByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(resourceService.findByUnitId(unitId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.findById(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        resourceService.create(resource);
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        Resource existingResource = resourceService.findById(id);
        if (existingResource == null) {
            return ResponseEntity.notFound().build();
        }
        
        resource.setId(id);
        resourceService.update(resource);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        Resource resource = resourceService.findById(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        resourceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Resource>> getResourcesByType(@PathVariable String type) {
        return ResponseEntity.ok(resourceService.findByResourceType(type));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<List<Resource>> getLatestResources(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(resourceService.findLatest(limit));
    }
}