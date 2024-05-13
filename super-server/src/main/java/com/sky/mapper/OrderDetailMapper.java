package com.sky.mapper;

import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetails);

    List<OrderDetail> selectByMap(HashMap<String, Object> params);
}
