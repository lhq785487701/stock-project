package com.framework.stock.service.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.WarehousingServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Warehousing;
import com.framework.stock.service.IWarehousingService;

@DubboService
public class WarehousingDubboServiceImpl implements WarehousingServiceApi {

	@Autowired
	private IWarehousingService warehousingService;

	@Override
	public ServiceResult applyWarehousing(Warehousing warehousing) {
		return ServiceResult.success(warehousingService.applyWarehousing(warehousing));
	}

	@Override
	public ServiceResult approveWarehousing(String id, String status, String approveUserId, String reason) {
		return ServiceResult.success(warehousingService.approveWarehousing(id, status, approveUserId, reason));
	}

	@Override
	public ServiceResult queryWarehousings(String warehousingNo) {
		return ServiceResult.success(warehousingService.queryWarehousings(warehousingNo));
	}

	@Override
	public ServiceResult getWarehousing(String warehousingNo) {
		return ServiceResult.success(warehousingService.getWarehousing(warehousingNo));
	}

	@Override
	public ServiceResult getWarehousingDetail(String warehousingNo) {
		return ServiceResult.success(warehousingService.getWarehousingDetail(warehousingNo));
	}



	
}
