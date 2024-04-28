package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
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
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        // 先查到所有的菜品
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);

        List<Dish> dishes = page.getResult();

        // 将Dish转换为DishVO
        List<DishVO> dishVOList = new ArrayList<>();
        dishes.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVOList.add(dishVO);
        });

        dishVOList.forEach(dishVO -> {
            // 为每个菜品设置所有相关联的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(dishVO.getId());
            dishVO.setFlavors(flavors);

            // 根据菜品分类id查询菜品分类名称
            Category category = categoryMapper.selectById(dishVO.getCategoryId());
            if (category != null) {
                dishVO.setCategoryName(category.getName());
            }
        });

        return new PageResult(dishVOList.size(), dishVOList);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        List<Dish> dishes = dishMapper.selectByDishIds(ids);
        dishes.forEach(dish -> {
            // 检查该菜品是否启售
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        List<SetmealDish> setmealDishes1 = setmealDishMapper.selectByDishIds(ids);
        if (setmealDishes1.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品
        dishMapper.deleteByDishIds(ids);

        // 删除菜品口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO findById(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish != null) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);

            // 查询菜品关联的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
            dishVO.setFlavors(flavors);

            // 查询菜品分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dishVO.setCategoryName(category.getName());
            }

            return dishVO;
        }
        return null;
    }


    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // 更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 先删除菜品关联的口味
        dishFlavorMapper.deleteByDishId(dish.getId());

        // 再插入新的菜品关联的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
        if (flavors != null && flavors.size() > 0) {
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
