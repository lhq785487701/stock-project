package com.framework.stock.api;

import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Warehouse;

/**
 * @Description 仓库接口
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface WarehouseServiceApi {
	
	/**
	 * @Description 查询仓库列表
	 * @param  name
	 * @param  code
	 * @return
	 */
	ServiceResult queryWarehouses(String name, String code);
	
	/**
	 * @Description 通过仓库id查询仓库
	 * @param id
	 * @return
	 */
	ServiceResult queryWarehouseById(String id);
	
	/**
	 * @Description 创建仓库信息
	 * @param warehouse
	 * @return
	 */
	ServiceResult createWarehouse(Warehouse warehouse);

}
