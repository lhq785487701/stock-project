package com.framework.stock.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.CheckWarehouseDetailMapper;
import com.framework.stock.model.CheckWarehouseDetail;
import com.framework.stock.service.ICheckWarehouseDetailService;

@Service
public class CheckWarehouseDetailServiceImpl extends ServiceImpl<CheckWarehouseDetailMapper, CheckWarehouseDetail>
		implements ICheckWarehouseDetailService {

}
