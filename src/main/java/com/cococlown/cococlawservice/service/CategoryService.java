package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 获取全部分类
     */
    List<Category> getAllCategories();

    /**
     * 根据ID获取分类
     */
    Category getCategoryById(Long id);
}
