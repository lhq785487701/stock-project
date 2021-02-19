package com.framework.stock.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.common.enums.ConstantEnum.IS_DELETE;
import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.SupplierMapper;
import com.framework.stock.model.Supplier;
import com.framework.stock.service.ISupplierService;
import com.framework.stock.utils.SnowFlakeUtils;
import com.framework.stock.utils.StringUtils;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier>
		implements ISupplierService {

	@Override
	public List<Supplier> querySuppliers(String name, String code) {

		LambdaQueryWrapper<Supplier> queryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(name)) {
			queryWrapper.like(Supplier::getName, name);
		}
		if (StringUtils.isNotEmpty(code)) {
			queryWrapper.like(Supplier::getCode, code);
		}
		return this.getBaseMapper().selectList(queryWrapper);
	}

	@Override
	public Supplier querySupplierById(String id) {

		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50003);
		}
		return this.getById(id);
	}

	@Override
	public boolean createSupplier(Supplier supplier) {

		if (StringUtils.isEmpty(supplier.getName())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50004);
		}
		if (StringUtils.isEmpty(supplier.getCode())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50005);
		}
		LambdaQueryWrapper<Supplier> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Supplier::getCode, supplier.getCode());
		if (this.count(queryWrapper) > 0) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50006);
		}

		supplier = supplier.toBuilder().id(SnowFlakeUtils.getId()).createTime(new Date()).build();
		return this.save(supplier);
	}

	@Override
	public boolean deleteSupplier(List<String> ids) {
		if (ids.isEmpty()) {
			log.debug("ids为空，不需要删除");
			return true;
		}
		List<Supplier> entityList = new ArrayList<>();
		for (String id : ids) {
			if (StringUtils.isEmpty(id)) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50003);
			}
			entityList.add(new Supplier().toBuilder()
					.id(id)
					.deleted(IS_DELETE.YES.value())
					.modifyTime(new Date()).build());
		}
		return this.updateBatchById(entityList);
	}

	@Override
	public boolean updateSupplier(Supplier supplier) {
		String id = supplier.getId();
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50003);
		}
		Supplier.SupplierBuilder entityBuilder = new Supplier().toBuilder().id(id);
		if (StringUtils.isNotEmpty(supplier.getName())) {
			entityBuilder.name(supplier.getName());
		}
		if (StringUtils.isNotEmpty(supplier.getAddress())) {
			entityBuilder.address(supplier.getAddress());
		}
		if (StringUtils.isNotEmpty(supplier.getContactEmail())) {
			entityBuilder.contactEmail(supplier.getContactEmail());
		}
		if (StringUtils.isNotEmpty(supplier.getContactName())) {
			entityBuilder.contactName(supplier.getContactName());
		}
		if (StringUtils.isNotEmpty(supplier.getContactPhone())) {
			entityBuilder.contactPhone(supplier.getContactPhone());
		}
		if (StringUtils.isNotEmpty(supplier.getFax())) {
			entityBuilder.fax(supplier.getFax());
		}
		if (StringUtils.isNotEmpty(supplier.getPostalCode())) {
			entityBuilder.postalCode(supplier.getPostalCode());
		}
		if (StringUtils.isNotEmpty(supplier.getTelephone())) {
			entityBuilder.telephone(supplier.getTelephone());
		}
		if (StringUtils.isNotEmpty(supplier.getRemark())) {
			entityBuilder.remark(supplier.getRemark());
		}
		return this.updateById(entityBuilder.build());
	}
}
