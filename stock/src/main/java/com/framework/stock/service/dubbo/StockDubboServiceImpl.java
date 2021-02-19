package com.framework.stock.service.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.StockServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.service.IStockService;

@DubboService
public class StockDubboServiceImpl implements StockServiceApi {

	@Autowired
	private IStockService stockService;

	@Override
	public ServiceResult queryStocks(String skuName) {
		return ServiceResult.success(stockService.queryStocks(skuName));
	}

	@Override
	public ServiceResult queryStockBySkuId(String warehouseId, String skuId) {
		return ServiceResult.success(stockService.queryStockBySkuId(warehouseId, skuId));
	}

	@Override
	public ServiceResult lockStock(String warehouseId, String skuId, Integer quantity, String orderNo) {
		return ServiceResult.success(stockService.lockStock(warehouseId, skuId, quantity, orderNo));
	}

	@Override
	public ServiceResult unlockStock(String warehouseId, String skuId, Integer quantity, String orderNo) {
		return ServiceResult.success(stockService.unlockStock(warehouseId, skuId, quantity, orderNo));
	}



	
}
