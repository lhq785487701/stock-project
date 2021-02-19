package com.framework.stock.service.dubbo;

import java.util.List;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.StockKeepingUnitServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.StandardProductUnit;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.service.IStockKeepingUnitService;

@DubboService
public class StockKeepingUnitDubboServiceImpl implements StockKeepingUnitServiceApi {

	@Autowired
	private IStockKeepingUnitService stockKeepingUnitService;

	@Override
	public ServiceResult createSku(List<StockKeepingUnit> skuList) {
		return ServiceResult.success(stockKeepingUnitService.createSku(skuList));
	}

	@Override
	public ServiceResult createSpu(List<StandardProductUnit> spuList) {
		return ServiceResult.success(stockKeepingUnitService.createSpu(spuList));
	}


	
}
