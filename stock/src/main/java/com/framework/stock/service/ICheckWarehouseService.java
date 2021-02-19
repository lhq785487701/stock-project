package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.CheckWarehouse;
import com.framework.stock.model.CheckWarehouseDetail;

/**
 * @Description 盘点接口
 * @author lihaoqi
 * @date 2020年12月7日 
 *
 */
public interface ICheckWarehouseService extends IService<CheckWarehouse> {
	
	/**
	 * @Description 盘点
	 * @param warehouseId 盘点的仓库
	 * @param type 盘点类型
	 * @param
	 */
	String checkWarehouse(String warehouseId, String type);
	
	/**
	 * @Description 手动盘点,盘点的方式，是统计所有的出库与入库的数量。与库存当前总量进行对比
	 * @param warehouseId 盘点的仓库
	 * @param
	 */
	String checkWarehouse(String warehouseId);

	/**
	 * @Description 通过盘点单号模糊查询盘点详情
	 * @param checkNo
	 * @return
	 */
	List<CheckWarehouse> queryCheckWarehouses(String checkNo);
	
	
	/**
	 * @Description 通过盘点单号查询列表
	 * @param checkNo
	 * @return
	 */
	List<CheckWarehouse> getCheckWarehouse(String checkNo);

	/**
	 * @Description 通过调盘点号查询盘点明细列表
	 * @param checkNo 
	 * @return
	 */
	List<CheckWarehouseDetail> getCheckWarehouseDetail(String checkNo);
}
