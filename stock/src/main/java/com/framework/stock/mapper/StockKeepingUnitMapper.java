package com.framework.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.framework.stock.model.StockKeepingUnit;

public interface StockKeepingUnitMapper extends BaseMapper<StockKeepingUnit> {

	StockKeepingUnit getSkuAndSpuBySkuId(String skuId);

}

