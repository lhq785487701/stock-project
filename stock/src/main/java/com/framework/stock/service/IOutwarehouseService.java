package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.Outwarehouse;
import com.framework.stock.model.OutwarehouseDetail;

/**
 * @Description 出库接口
 * @author lihaoqi
 * @date 2020年12月2日 
 *
 */
public interface IOutwarehouseService extends IService<Outwarehouse> {

	/**
	 * @Description 申请出库
	 * @param warehousing
	 * @param
	 */
	String applyOutwarehouse(Outwarehouse outwarehouse);

	/**
	 * @Description 出库审批
	 * @param id            出库id
	 * @param status        审批状态 @see APPLY_STATUS
	 * @param approveUserId
	 * @param reason
	 * @return
	 */
	String approveOutwarehouse(String id, String status, String approveUserId, String reason);

	/**
	 * @Description 通过出库单号模糊查询出库单详情
	 * @param outwarehouseNo
	 * @return
	 */
	List<Outwarehouse> queryOutwarehouses(String outwarehouseNo);

	/**
	 * @Description 通过出库单号查询列表
	 * @param outwarehouseNo
	 * @return
	 */
	List<Outwarehouse> getOutwarehouse(String outwarehouseNo);

	/**
	 * @Description 通过出库单号查询出库明细列表
	 * @param outwarehouseNo
	 * @return
	 */
	List<OutwarehouseDetail> getOutwarehouseDetail(String outwarehouseNo);
}
