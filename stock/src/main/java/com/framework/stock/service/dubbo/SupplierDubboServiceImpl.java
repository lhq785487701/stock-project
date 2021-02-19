package com.framework.stock.service.dubbo;

import java.util.Arrays;
import java.util.List;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import com.framework.stock.api.SupplierServiceApi;
import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.Supplier;
import com.framework.stock.service.ISupplierService;

@DubboService
public class SupplierDubboServiceImpl implements SupplierServiceApi {

	@Autowired
	private ISupplierService supplierService;

	@Override
	public ServiceResult querySuppliers(String name, String code) {
		return ServiceResult.success(supplierService.querySuppliers(name, code));
	}

	@Override
	public ServiceResult querySupplierById(String id) {
		return ServiceResult.success(supplierService.querySupplierById(id));
	}

	@Override
	public ServiceResult createSupplier(Supplier supplier) {
		return ServiceResult.success(supplierService.createSupplier(supplier));
	}

	@Override
	public ServiceResult deleteSuppliers(List<String> ids) {
		return ServiceResult.success(supplierService.deleteSupplier(ids));
	}

	@Override
	public ServiceResult deleteSupplier(String id) {
		return ServiceResult.success(supplierService.deleteSupplier(Arrays.asList(id)));
	}

	@Override
	public ServiceResult updateSupplier(Supplier supplier) {
		return ServiceResult.success(supplierService.updateSupplier(supplier));
	}
	
}
