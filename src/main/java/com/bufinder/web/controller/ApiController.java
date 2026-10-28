package com.bufinder.web.controller;

import com.bufinder.web.service.CategoryDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class ApiController {

    private final CategoryDataService categoryDataService;

    public ApiController(CategoryDataService categoryDataService) {
        this.categoryDataService = categoryDataService;
    }

    @GetMapping("/subtypes")
    public ResponseEntity<List<String>> getSubTypes(@RequestParam(name = "category", required = false) String category) {
        return ResponseEntity.ok(categoryDataService.getSubTypes(category));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getBrands(@RequestParam(name = "category", required = false) String category) {
        return ResponseEntity.ok(categoryDataService.getBrands(category));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCategoryData() {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoryDataService.getCategories());
        data.put("subTypes", categoryDataService.getAllSubTypesMap());
        data.put("brands", categoryDataService.getAllBrandsMap());
        data.put("colors", categoryDataService.getColors());
        return ResponseEntity.ok(data);
    }
}
