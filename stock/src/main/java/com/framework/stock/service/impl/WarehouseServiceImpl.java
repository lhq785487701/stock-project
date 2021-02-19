package com.framework.stock.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.common.enums.ConstantEnum.IS_DELETE;
import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.WarehouseMapper;
import com.framework.stock.model.Stock;
import com.framework.stock.model.Warehouse;
import com.framework.stock.service.IStockService;
import com.framework.stock.service.IWarehouseService;
import com.framework.stock.utils.SnowFlakeUtils;
import com.framework.stock.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse>
		implements IWarehouseService {
	
	@Autowired
	private IStockService stockService;

	@Override
	public List<Warehouse> queryWarehouses(String name, String code) {

		LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(name)) {
			queryWrapper.like(Warehouse::getName, name);
		}
		if (StringUtils.isNotEmpty(code)) {
			queryWrapper.like(Warehouse::getCode, code);
		}
		return this.getBaseMapper().selectList(queryWrapper);
	}

	@Override
	public Warehouse queryWarehouseById(String id) {

		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50007);
		}
		return this.getById(id);
	}

	@Override
	public boolean createWarehouse(Warehouse warehouse) {

		if (StringUtils.isEmpty(warehouse.getCode())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50008);
		}
		LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Warehouse::getCode, warehouse.getCode());
		if (this.count(queryWrapper) > 0) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50009);
		}
		if (StringUtils.isEmpty(warehouse.getName())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50010);
		}
		if (StringUtils.isEmpty(warehouse.getCountry())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50011);
		}
		if (StringUtils.isEmpty(warehouse.getProvince())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50012);
		}
		if (StringUtils.isEmpty(warehouse.getCity())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50013);
		}
		if (StringUtils.isEmpty(warehouse.getArea())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50014);
		}
		if (StringUtils.isEmpty(warehouse.getAddress())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50015);
		}
		if (StringUtils.isEmpty(warehouse.getTelephone())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50016);
		}
		if (StringUtils.isEmpty(warehouse.getCreateUser())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50017);
		}

		warehouse = warehouse.toBuilder().id(SnowFlakeUtils.getId()).createTime(new Date()).build();
		return this.save(warehouse);
	}

	@Override
	public boolean deleteWarehouse(String id) {
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50003);
		}
		List<Stock> stockList = stockService.queryStocksByWarehouse(id);
		if (!stockList.isEmpty()) {
			// 判断是否存在可用库存或者预留库存，如果存在则不允许删除该仓库,并不影响其他仓库的删除
			int count = stockList.stream().map(Stock::getAvailableQuantity).reduce(0, Integer::sum)
					+ stockList.stream().map(Stock::getReservedQuantity).reduce(0, Integer::sum);
			if (count > 0) {
				log.error("仓库{}还存在可用或者预留库存，无法删除该仓库", id);
				return false;
			}
		}

		Warehouse warehouse = new Warehouse().toBuilder()
				.id(id)
				.deleted(IS_DELETE.YES.value())
				.modifyTime(new Date())
				.build();
		return this.updateById(warehouse);
	}
}
