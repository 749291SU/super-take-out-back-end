package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.result.PageResult;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * @projectName: super-takeout
 * @package: com.sky.service
 * @className: DishService
 * @author: 749291
 * @description: TODO
 * @date: 4/27/2024 15:36
 * @version: 1.0
 */

public interface DishService {
    public void saveWithFlavor(DishDTO dishDTo);
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(Long[] ids);

    DishVO findById(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    List<DishVO> listWithFlavor(Dish dish);

    void updateStatus(Long id, Integer status);

    List<Dish> listByCategoryId(Long categoryId);
}
