package com.framework.stock.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.OutwarehouseDetailMapper;
import com.framework.stock.model.OutwarehouseDetail;
import com.framework.stock.service.IOutwarehouseDetailService;

@Service
public class OutwarehouseDetailServiceImpl extends ServiceImpl<OutwarehouseDetailMapper, OutwarehouseDetail>
		implements IOutwarehouseDetailService {

}
