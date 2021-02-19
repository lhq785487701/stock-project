package com.framework.stock.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.OutwarehouseLogMapper;
import com.framework.stock.model.OutwarehouseLog;
import com.framework.stock.service.IOutwarehouseLogService;

@Service
public class OutwarehouseLogServiceImpl extends ServiceImpl<OutwarehouseLogMapper, OutwarehouseLog>
		implements IOutwarehouseLogService {

}
