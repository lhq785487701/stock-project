package com.framework.stock.service.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.WarehouseServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Warehouse;
import com.framework.stock.service.IWarehouseService;

@DubboService
public class WarehouseDubboServiceImpl implements WarehouseServiceApi {

	@Autowired
	private IWarehouseService warehouseService;

	@Override
	public ServiceResult queryWarehouses(String name, String code) {
		return ServiceResult.success(warehouseService.queryWarehouses(name, code));
	}

	@Override
	public ServiceResult queryWarehouseById(String id) {
		return ServiceResult.success(warehouseService.queryWarehouseById(id));
	}

	@Override
	public ServiceResult createWarehouse(Warehouse warehouse) {
		return ServiceResult.success(warehouseService.createWarehouse(warehouse));
	}



	
}
