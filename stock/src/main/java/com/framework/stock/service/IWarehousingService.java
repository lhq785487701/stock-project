package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.Warehousing;
import com.framework.stock.model.WarehousingDetail;

/**
 * @Description 入库接口
 * @author lihaoqi
 * @date 2020年12月2日 
 *
 */
public interface IWarehousingService extends IService<Warehousing> {

	/**
	 * @Description 申请入库
	 * @param warehousing
	 * @param
	 */
	String applyWarehousing(Warehousing warehousing);

	/**
	 * @Description 入库审批
	 * @param id            入库id
	 * @param status        审批状态 @see APPLY_STATUS
	 * @param approveUserId
	 * @param reason
	 * @return
	 */
	String approveWarehousing(String id, String status, String approveUserId, String reason);

	/**
	 * @Description 通过入库单号模糊查询入库单详情
	 * @param warehousingNo
	 * @return
	 */
	List<Warehousing> queryWarehousings(String warehousingNo);

	/**
	 * @Description 通过入库单号查询列表
	 * @param warehousingNo
	 * @return
	 */
	List<Warehousing> getWarehousing(String warehousingNo);

	/**
	 * @Description 通过入库单号查询入库明细列表
	 * @param warehousingNo
	 * @return
	 */
	List<WarehousingDetail> getWarehousingDetail(String warehousingNo);
}
