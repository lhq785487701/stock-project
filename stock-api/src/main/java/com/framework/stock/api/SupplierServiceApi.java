package com.framework.stock.api;

import java.util.List;

import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Supplier;

/**
 * @Description 供应商接口类
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface SupplierServiceApi {
	
	/**
	 * @Description 查询供应商列表,该查询接口会查询出所有包括删除的数据，业务系统做筛选
	 * @param name
	 * @param code
	 * @return
	 */
	ServiceResult querySuppliers(String name, String code);

	/**
	 * @Description 通过供应商id查询供应商
	 * @param id
	 * @return
	 */
	ServiceResult querySupplierById(String id);
	
	/**
	 * @Description 创建供应商
	 * @param supplier
	 * @return
	 */
	ServiceResult createSupplier (Supplier supplier);
	
	/**
	 * @Description 批量删除供应商
	 * @param ids
	 * @return
	 */
	ServiceResult deleteSuppliers(List<String> ids);
	
	/**
	 * @Description 删除供应商
	 * @param id
	 * @return
	 */
	ServiceResult deleteSupplier(String id);
	
	/**
	 * @Description 修改供应商信息
	 * @param supplier
	 * @return
	 */
	ServiceResult updateSupplier(Supplier supplier);
}
