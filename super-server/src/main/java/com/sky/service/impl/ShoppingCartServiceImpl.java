package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @projectName: super-takeout
 * @package: com.sky.service.impl
 * @className: ShoppingCartServiceImpl
 * @author: 749291
 * @description: TODO
 * @date: 5/8/2024 19:14
 * @version: 1.0
 */

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setUserId(BaseContext.getCurrentId());

        if (shoppingCartDTO.getSetmealId() != null) {
            // 根据用户id和套餐id查询购物车
            List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByEntity(shoppingCart);
            if (shoppingCarts != null && shoppingCarts.size() > 0) {
                ShoppingCart cart = shoppingCarts.get(0);
                // 如果购物车中已经存在该套餐，则更新数量+1
                cart.setNumber(cart.getNumber() + 1);
                shoppingCartMapper.update(cart);
            } else {
                // 如果购物车中不存在该套餐，则新增
                // 设置名称为套餐名称
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
                shoppingCartMapper.insert(shoppingCart);
            }
        } else if (shoppingCartDTO.getDishId() != null) {
            // 根据用户id和菜品id查询购物车
            List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByEntity(shoppingCart);
            if (shoppingCarts != null && shoppingCarts.size() > 0) {
                ShoppingCart cart = shoppingCarts.get(0);
                // 如果购物车中已经存在该菜品，则更新数量+1
                cart.setNumber(cart.getNumber() + 1);
                shoppingCartMapper.update(cart);
            } else {
                // 如果购物车中不存在该菜品，则新增
                // 设置名称为菜品名称
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCartMapper.insert(shoppingCart);
            }
        }
    }

    @Override
    public List<ShoppingCart> list(Long userId) {
        List<ShoppingCart> list = shoppingCartMapper.list(userId);
        return list;
    }

    @Override
    public void clean(Long userId) {
        shoppingCartMapper.deleteByUserId(userId);
    }
}
