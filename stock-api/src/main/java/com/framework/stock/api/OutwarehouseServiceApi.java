package com.framework.stock.api;

import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Outwarehouse;

/**
 * @Description 出库接口
 * @author lihaoqi
 * @date 2020年12月3日
 *
 */
public interface OutwarehouseServiceApi {

	/**
	 * @Description 申请出库
	 * @param warehousing
	 * @param
	 */
	ServiceResult applyOutwarehouse(Outwarehouse outwarehouse);

	/**
	 * @Description 出库审批
	 * @param id            出库id
	 * @param status        审批状态 @see APPLY_STATUS
	 * @param approveUserId
	 * @param reason
	 * @return
	 */
	ServiceResult approveOutwarehouse(String id, String status, String approveUserId, String reason);

	/**
	 * @Description 通过出库单号模糊查询出库单详情
	 * @param outwarehouseNo
	 * @return
	 */
	ServiceResult queryOutwarehouses(String outwarehouseNo);

	/**
	 * @Description 通过出库单号查询列表
	 * @param outwarehouseNo
	 * @return
	 */
	ServiceResult getOutwarehouse(String outwarehouseNo);

	/**
	 * @Description 通过出库单号查询出库明细列表
	 * @param outwarehouseNo
	 * @return
	 */
	ServiceResult getOutwarehouseDetail(String outwarehouseNo);
}
