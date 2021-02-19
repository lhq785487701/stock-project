package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.model.AllotWarehouse;
import com.framework.stock.model.AllotWarehouseDetail;

/**
 * @Description 调仓接口
 * @author lihaoqi
 * @date 2020年12月2日 
 *
 */
public interface IAllotWarehouseService extends IService<AllotWarehouse> {

	/**
	 * @Description 调仓申请
	 * @param allotWarehouse
	 * @param
	 */
	String applyAllotWarehouse(AllotWarehouse allotWarehouse) throws ServiceException;

	/**
	 * @Description 调仓审批
	 * @param id            出库id
	 * @param status        审批状态 @see APPLY_STATUS
	 * @param approveUserId
	 * @param reason
	 * @return
	 */
	String approveAllotWarehouse(String id, String status, String approveUserId, String reason) throws ServiceException;

	/**
	 * @Description 通过调仓单号模糊查询调仓详情
	 * @param allotNo
	 * @return
	 */
	List<AllotWarehouse> queryAllotWarehouses(String allotNo);
	
	
	/**
	 * @Description 通过调仓单号查询列表
	 * @param allotNo
	 * @return
	 */
	List<AllotWarehouse> getAllotWarehouse(String allotNo);

	/**
	 * @Description 通过调仓单号查询调仓明细列表
	 * @param allotWarehouse 
	 * @return
	 */
	List<AllotWarehouseDetail> getAllotWarehouseDetail(String allotNo);
}
