package com.framework.stock.service.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.AllotWarehouseServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.AllotWarehouse;
import com.framework.stock.service.IAllotWarehouseService;

@DubboService
public class AllotWarehouseDubboServiceImpl implements AllotWarehouseServiceApi {

	@Autowired
	private IAllotWarehouseService allotWarehouseService;


	@Override
	public ServiceResult applyAllotWarehouse(AllotWarehouse allotWarehouse)  {
		return ServiceResult.success(allotWarehouseService.applyAllotWarehouse(allotWarehouse));
	}

	@Override
	public ServiceResult approveAllotWarehouse(String id, String status, String approveUserId, String reason) {
		return ServiceResult.success(allotWarehouseService.approveAllotWarehouse(id, status, approveUserId, reason));
	}
	
	@Override
	public ServiceResult queryAllotWarehouses(String allotNo) {
		return ServiceResult.success(allotWarehouseService.queryAllotWarehouses(allotNo));
	}

	@Override
	public ServiceResult getAllotWarehouse(String allotNo) {
		return ServiceResult.success(allotWarehouseService.getAllotWarehouse(allotNo));
	}

	@Override
	public ServiceResult getAllotWarehouseDetail(String allotNo) {
		return ServiceResult.success(allotWarehouseService.getAllotWarehouseDetail(allotNo));
	}

}
