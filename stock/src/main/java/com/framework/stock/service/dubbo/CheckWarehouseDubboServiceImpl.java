package com.framework.stock.service.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.CheckWarehouseServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.service.ICheckWarehouseService;

@DubboService
public class CheckWarehouseDubboServiceImpl implements CheckWarehouseServiceApi {

	@Autowired
	private ICheckWarehouseService checkWarehouseService;

	@Override
	public ServiceResult checkWarehouse(String warehouseId) {
		return ServiceResult.success(checkWarehouseService.checkWarehouse(warehouseId));
	}

	@Override
	public ServiceResult queryCheckWarehouses(String checkNo) {
		return ServiceResult.success(checkWarehouseService.queryCheckWarehouses(checkNo));
	}

	@Override
	public ServiceResult getCheckWarehouse(String checkNo) {
		return ServiceResult.success(checkWarehouseService.getCheckWarehouse(checkNo));
	}

	@Override
	public ServiceResult getCheckWarehouseDetail(String checkNo) {
		return ServiceResult.success(checkWarehouseService.getCheckWarehouse(checkNo));
	}


	
}
