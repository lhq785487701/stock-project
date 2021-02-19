package com.framework.stock.api;

import com.framework.stock.common.response.ServiceResult;

/**
 * @Description 盘点库存接口
 * @author lihaoqi
 * @date 2020年12月7日
 *
 */
public interface CheckWarehouseServiceApi {

	/**
	 * @Description 手动盘点,盘点的方式，是统计所有的出库与入库的数量。与库存当前总量进行对比
	 * @param warehouseId 盘点的仓库
	 * @param
	 */
	ServiceResult checkWarehouse(String warehouseId);

	/**
	 * @Description 通过盘点单号模糊查询盘点详情
	 * @param checkNo
	 * @return
	 */
	ServiceResult queryCheckWarehouses(String checkNo);
	
	
	/**
	 * @Description 通过盘点单号查询列表
	 * @param checkNo
	 * @return
	 */
	ServiceResult getCheckWarehouse(String checkNo);

	/**
	 * @Description 通过调盘点号查询盘点明细列表
	 * @param checkNo 
	 * @return
	 */
	ServiceResult getCheckWarehouseDetail(String checkNo);

}
