package com.framework.stock.api;

import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Warehousing;

/**
 * @Description 入库接口
 * @author lihaoqi
 * @date 2020年12月3日
 *
 */
public interface WarehousingServiceApi {

	/**
	 * @Description 申请入库
	 * @param warehousing
	 * @param
	 */
	ServiceResult applyWarehousing(Warehousing warehousing);

	/**
	 * @Description 入库审批
	 * @param id            入库id
	 * @param status        审批状态 @see APPLY_STATUS
	 * @param approveUserId
	 * @param reason
	 * @return
	 */
	ServiceResult approveWarehousing(String id, String status, String approveUserId, String reason);

	/**
	 * @Description 通过入库单号模糊查询入库单详情
	 * @param warehousingNo
	 * @return
	 */
	ServiceResult queryWarehousings(String warehousingNo);

	/**
	 * @Description 通过入库单号查询列表
	 * @param warehousingNo
	 * @return
	 */
	ServiceResult getWarehousing(String warehousingNo);

	/**
	 * @Description 通过入库单号查询入库明细列表
	 * @param warehousingNo
	 * @return
	 */
	ServiceResult getWarehousingDetail(String warehousingNo);
}
