package com.sky.mapper;

import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    List<Orders> selectByEntity(Orders order);

    void deleteBatch(List<Long> orderIds);

    void updateById(Orders order);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    // 查询指定日期的所有已完成的订单总金额
    BigDecimal getTurnoverByMap(Map<String, Object> params);

    Integer getOrderAmountByMap(HashMap<String, Object> params);
}