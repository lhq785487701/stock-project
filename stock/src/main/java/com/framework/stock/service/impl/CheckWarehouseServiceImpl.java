package com.framework.stock.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.constant.Constants;
import com.framework.stock.constant.StockConstantEnum.CHECK_STOCK_STATUS;
import com.framework.stock.constant.StockConstantEnum.CHECK_STOCK_TYPE;
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.CheckWarehouseMapper;
import com.framework.stock.mapper.StockKeepingUnitMapper;
import com.framework.stock.model.CheckWarehouse;
import com.framework.stock.model.CheckWarehouseDetail;
import com.framework.stock.model.OutwarehouseLog;
import com.framework.stock.model.Stock;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.model.Warehouse;
import com.framework.stock.model.WarehousingLog;
import com.framework.stock.service.ICheckWarehouseDetailService;
import com.framework.stock.service.ICheckWarehouseService;
import com.framework.stock.service.IOutwarehouseLogService;
import com.framework.stock.service.IStockService;
import com.framework.stock.service.IWarehouseService;
import com.framework.stock.service.IWarehousingLogService;
import com.framework.stock.utils.OrderNoUtils;
import com.framework.stock.utils.SnowFlakeUtils;
import com.framework.stock.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CheckWarehouseServiceImpl extends ServiceImpl<CheckWarehouseMapper, CheckWarehouse>
		implements ICheckWarehouseService {

	@Autowired
	private ICheckWarehouseDetailService checkDetailService;
	
	@Autowired
	private IWarehouseService warehouseService;

	@Autowired
	private IStockService stockService;

	@Autowired
	private StockKeepingUnitMapper skuMapping;
	
	@Autowired
	private IWarehousingLogService warehousingLogService;
	
	@Autowired
	private IOutwarehouseLogService outwarehouseLogService;


	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String checkWarehouse(String warehouseId, String type) {
		// 盘点仓库
		if (StringUtils.isEmpty(warehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50007);
		}
		Warehouse warehouse = warehouseService.getById(warehouseId);
		if (warehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		// 盘点信息
		String checkNo = OrderNoUtils.createCheckWarehouseNo();
		String id = SnowFlakeUtils.getId();
		Date startTime = new Date();
		int totalCount = 0;
		int totalQuantity = 0;
		String status = CHECK_STOCK_STATUS.NORMAL.value();
		List<CheckWarehouseDetail> detailList = null;
		
		// 查询仓库下的库存明细
		LambdaQueryWrapper<Stock> stockQueryWrapper = new LambdaQueryWrapper<>();
		stockQueryWrapper.eq(Stock::getWarehouseId, warehouseId);
		List<Stock> stockList = stockService.list(stockQueryWrapper);
		if (stockList.isEmpty()) {
			log.info("该仓库暂无库存");
		} else {
			// 根据sku多少初始化list
			detailList = new ArrayList<>(stockList.size());
			// 在for循环外获取所有商品出入库信息
			List<String> skuIdList = stockList.stream().map(Stock::getSkuId).collect(Collectors.toList());
			LambdaQueryWrapper<WarehousingLog> warehousingQueryWrapper = new LambdaQueryWrapper<>();
			warehousingQueryWrapper.in(WarehousingLog::getSkuId, skuIdList);
			warehousingQueryWrapper.eq(WarehousingLog::getWarehouseId, warehouseId);
			List<WarehousingLog> warehousingList = warehousingLogService.list(warehousingQueryWrapper);
			LambdaQueryWrapper<OutwarehouseLog> outwarehouseQueryWrapper = new LambdaQueryWrapper<>();
			outwarehouseQueryWrapper.in(OutwarehouseLog::getSkuId, skuIdList);
			outwarehouseQueryWrapper.eq(OutwarehouseLog::getWarehouseId, warehouseId);
			List<OutwarehouseLog> outwarehouseList = outwarehouseLogService.list(outwarehouseQueryWrapper);
			for (Stock stock : stockList) {
				// 获取商品和库存数量
				String skuId = stock.getSkuId();
				int quantity = stock.getTotalQuantity();
				String message = null;
				String detailStatus = CHECK_STOCK_STATUS.NORMAL.value();
				// 盘点商品
				StockKeepingUnit sku = skuMapping.selectById(skuId);
				if (sku == null) {
					detailStatus = CHECK_STOCK_STATUS.UNUSUAL.value();
					status = detailStatus;
					message = Constants.STOCK_SKU_ERROR;
					CheckWarehouseDetail detail = new CheckWarehouseDetail().toBuilder()
							.checkNo(checkNo)
							.skuId(skuId)
							.status(detailStatus)
							.message(message)
							.enterQuantity(0)
							.outQuantity(0)
							.totalQuantity(quantity).build();
					detailList.add(detail);
					continue;
				}
				
				// 获取对应的skuid的出入库数量
				int enterQuantity = warehousingList.stream().filter(w -> w.getSkuId().equals(skuId))
						.map(WarehousingLog::getQuantity).reduce(0, Integer::sum);
				int outQuantity = outwarehouseList.stream().filter(w -> w.getSkuId().equals(skuId))
						.map(OutwarehouseLog::getQuantity).reduce(0, Integer::sum);
				if (quantity != enterQuantity - outQuantity) {
					detailStatus = CHECK_STOCK_STATUS.UNUSUAL.value();
					status = detailStatus;
					message = Constants.STOCK_QUANTITY_ERROR;
				}
				CheckWarehouseDetail detail = new CheckWarehouseDetail().toBuilder()
						.checkNo(checkNo)
						.skuId(skuId)
						.status(detailStatus)
						.message(message)
						.enterQuantity(enterQuantity)
						.outQuantity(outQuantity)
						.totalQuantity(quantity).build();
				detailList.add(detail);
				// 增加盘点单总数量与状态
				totalCount++;
				totalQuantity += quantity;
			}
		}

		CheckWarehouse checkHouse = new CheckWarehouse().toBuilder()
				.id(id)
				.checkNo(checkNo)
				.startTime(startTime)
				.warehouseId(warehouseId)
				.status(status)
				.createTime(startTime)
				.modifyTime(startTime)
				.finishedTime(new Date())
				.totalCount(totalCount)
				.totalQuantity(totalQuantity).build();
		// 保存盘点信息
		this.save(checkHouse);
		if (detailList != null) {
			// 保存盘点详情
			checkDetailService.saveBatch(detailList);
		}
		
		return checkNo;
	}
	
	@Override
	public String checkWarehouse(String warehouseId) {
		log.info("[CheckWarehouseServiceApi] -> checkWarehouse start");
		String checkNo = checkWarehouse(warehouseId, CHECK_STOCK_TYPE.CONTROL.value());
		log.info("[CheckWarehouseServiceApi] -> checkWarehouse success");
		return checkNo;
	}

	@Override
	public List<CheckWarehouse> queryCheckWarehouses(String checkNo) {
		// 查询列表
		LambdaQueryWrapper<CheckWarehouse> checkWarehouseQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(checkNo)) {
			checkWarehouseQueryWrapper.like(CheckWarehouse::getCheckNo, checkNo);
		}
		List<CheckWarehouse> checkWarehouseList = this.list(checkWarehouseQueryWrapper);
		List<String> checkNoList = checkWarehouseList.stream().map(CheckWarehouse::getCheckNo)
				.collect(Collectors.toList());

		// 查询明细
		LambdaQueryWrapper<CheckWarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.in(CheckWarehouseDetail::getCheckNo, checkNoList);
		List<CheckWarehouseDetail> detailList = checkDetailService.list(detailQueryWrapper);

		for (int i = 0; i < checkWarehouseList.size(); i++) {
			CheckWarehouse checkWarehouse = checkWarehouseList.get(i);
			String CNo = checkWarehouse.getCheckNo();

			List<CheckWarehouseDetail> cdetailList = detailList.stream().filter(w -> w.getCheckNo().equals(CNo))
					.collect(Collectors.toList());

			checkWarehouse.setCheckWarehouseDetailList(cdetailList);
		}
		return checkWarehouseList;
	}

	@Override
	public List<CheckWarehouse> getCheckWarehouse(String checkNo) {
		// 查询列表
		LambdaQueryWrapper<CheckWarehouse> checkWarehouseQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(checkNo)) {
			checkWarehouseQueryWrapper.like(CheckWarehouse::getCheckNo, checkNo);
		}
		List<CheckWarehouse> checkWarehouseList = this.list(checkWarehouseQueryWrapper);
		return checkWarehouseList;
	}

	@Override
	public List<CheckWarehouseDetail> getCheckWarehouseDetail(String checkNo) {
		if (StringUtils.isEmpty(checkNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50324);
		}
		// 查询明细
		LambdaQueryWrapper<CheckWarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.eq(CheckWarehouseDetail::getCheckNo, checkNo);
		List<CheckWarehouseDetail> detailList = checkDetailService.list(detailQueryWrapper);
		
		return detailList;
	}

}
