package com.framework.stock.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.StockChangeLogMapper;
import com.framework.stock.model.StockChangeLog;
import com.framework.stock.service.IStockChangeLogService;

/**
 * @Description 库存日志
 * @author lihaoqi
 * @date 2020年12月2日
 *
 */
@Service
public class StockChangeLogServiceImpl extends ServiceImpl<StockChangeLogMapper, StockChangeLog>
		implements IStockChangeLogService {

}
