package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

/**
 * @projectName: super-takeout
 * @package: com.sky.service.impl
 * @className: DishServiceImpl
 * @author: 749291
 * @description: TODO
 * @date: 4/27/2024 15:36
 * @version: 1.0
 */

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTo) {
        // 向菜品表中插入数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTo, dish);
        dishMapper.insert(dish);

        // 向菜品口味表中插入数据
        List<DishFlavor> flavors = dishDTo.getFlavors();
        // 设置菜品id
        flavors.forEach(flavor -> flavor.setDishId(dish.getId()));

        if (flavors != null && flavors.size() > 0) {
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
