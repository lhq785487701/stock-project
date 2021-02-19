package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.Warehouse;

/**
 * @Description 仓库接口
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface IWarehouseService extends IService<Warehouse> {

	/**
	 * @Description 查询仓库列表
	 * @param  name
	 * @param  code
	 * @return
	 */
	List<Warehouse> queryWarehouses(String name, String code);
	
	/**
	 * @Description 通过仓库id查询仓库
	 * @param id
	 * @return
	 */
	Warehouse queryWarehouseById(String id);
	
	/**
	 * @Description 创建仓库信息
	 * @param warehouse
	 * @return
	 */
	boolean createWarehouse(Warehouse warehouse);
	
	/**
	 * @Description 删除仓库,仓库不支持批量删除，因为存在判断库存信息
	 * @param id
	 * @return
	 */
	boolean deleteWarehouse(String id);
}
