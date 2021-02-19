package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.Supplier;

/**
 * @Description 供应商接口类
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface ISupplierService extends IService<Supplier> {
	
	/**
	 * @Description 查询供应商列表
	 * @param name
	 * @param code
	 * @return
	 */
	List<Supplier> querySuppliers(String name, String code);

	/**
	 * @Description 通过供应商id查询供应商
	 * @param id
	 * @return
	 */
	Supplier querySupplierById(String id);

	/**
	 * @Description 创建供应商
	 * @param supplier
	 * @return
	 */
	boolean createSupplier(Supplier supplier);
	
	/**
	 * @Description 删除供应商
	 * @param ids
	 * @return
	 */
	boolean deleteSupplier(List<String> ids);
	
	/**
	 * @Description 修改供应商信息
	 * @param supplier
	 * @return
	 */
	boolean updateSupplier(Supplier supplier);
}
