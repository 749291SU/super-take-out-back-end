package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    void deleteByDishIds(@Param("dishIds") Long[] ids);

    List<Dish> selectByDishIds(@Param("dishIds") Long[] ids);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> list(Dish dish);
}