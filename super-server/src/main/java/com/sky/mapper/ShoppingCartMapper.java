package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    @Select("select * from shopping_cart where user_id = #{userId} and setmeal_id = #{setmealId}")
    ShoppingCart getByUserIdAndSetmealId(Long userId, Long setmealId);

    void update(ShoppingCart cart);

    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId} and dish_id = #{dishId}")
    ShoppingCart getByUserIdAndDishId(Long userId, Long dishId);

    List<ShoppingCart> selectByEntity(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> list(Long userId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
