package com.framework.stock.api;

import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.AllotWarehouse;

/**
 * @Description 调拨库存接口
 * @author lihaoqi
 * @date 2020年12月6日
 *
 */
public interface AllotWarehouseServiceApi {

	/**
	 * @Description 调仓申请
	 * @param allotWarehouse
	 * @param
	 */
	ServiceResult applyAllotWarehouse(AllotWarehouse allotWarehouse);

	/**
	 * @Description 调仓审批
	 * @param id            出库id
	 * @param status        审批状态 @see APPLY_STATUS
	 * @param approveUserId
	 * @param reason
	 * @return
	 */
	ServiceResult approveAllotWarehouse(String id, String status, String approveUserId, String reason);

	/**
	 * @Description 通过调仓单号模糊查询调仓详情
	 * @param allotNo
	 * @return
	 */
	ServiceResult queryAllotWarehouses(String allotNo);
	
	
	/**
	 * @Description 通过调仓单号查询列表
	 * @param allotNo
	 * @return
	 */
	ServiceResult getAllotWarehouse(String allotNo);

	/**
	 * @Description 通过调仓单号查询调仓明细列表
	 * @param allotWarehouse 
	 * @return
	 */
	ServiceResult getAllotWarehouseDetail(String allotNo);

}
