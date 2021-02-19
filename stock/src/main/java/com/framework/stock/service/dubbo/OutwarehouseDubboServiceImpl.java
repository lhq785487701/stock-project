package com.framework.stock.service.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.OutwarehouseServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Outwarehouse;
import com.framework.stock.service.IOutwarehouseService;

@DubboService
public class OutwarehouseDubboServiceImpl implements OutwarehouseServiceApi {

	@Autowired
	private IOutwarehouseService outwarehouseService;

	@Override
	public ServiceResult applyOutwarehouse(Outwarehouse outwarehouse) {
		return ServiceResult.success(outwarehouseService.applyOutwarehouse(outwarehouse));
	}

	@Override
	public ServiceResult approveOutwarehouse(String id, String status, String approveUserId, String reason) {
		return ServiceResult.success(outwarehouseService.approveOutwarehouse(id, status, approveUserId, reason));
	}

	@Override
	public ServiceResult queryOutwarehouses(String outwarehouseNo) {
		return ServiceResult.success(outwarehouseService.queryOutwarehouses(outwarehouseNo));
	}

	@Override
	public ServiceResult getOutwarehouse(String outwarehouseNo) {
		return ServiceResult.success(outwarehouseService.getOutwarehouse(outwarehouseNo));
	}

	@Override
	public ServiceResult getOutwarehouseDetail(String outwarehouseNo) {
		return ServiceResult.success(outwarehouseService.getOutwarehouseDetail(outwarehouseNo));
	}

	
}
