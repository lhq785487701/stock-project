package com.framework.stock.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.framework.stock.model.Warehouse;

public interface WarehouseMapper extends BaseMapper<Warehouse> {

    List<Warehouse> selectUserList(Page<?> page, String name);
}

