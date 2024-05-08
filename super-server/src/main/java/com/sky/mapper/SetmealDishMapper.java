package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    @Select("select * from setmeal_dish where dish_id = #{id}")
    List<SetmealDish> selectByDishId(Long id);

    List<SetmealDish> selectByDishIds(@Param("dishIds") Long[] ids);

    void insert(SetmealDish setmealDish);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectBySetmealId(Long id);

    void deleteBatchBySetmealIds(@Param("setmealIds") Long[] ids);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteAllBySetmealId(Long id);
}