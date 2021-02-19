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
import com.framework.stock.constant.StockConstantEnum.APPLY_STATUS;
import com.framework.stock.constant.StockConstantEnum.OUTWAREHOUSE_TYPE;
import com.framework.stock.constant.StockConstantEnum.WAREHOUSING_TYPE;
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.AllotWarehouseMapper;
import com.framework.stock.mapper.StockKeepingUnitMapper;
import com.framework.stock.model.AllotWarehouse;
import com.framework.stock.model.AllotWarehouseDetail;
import com.framework.stock.model.OutwarehouseLog;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.model.Warehouse;
import com.framework.stock.model.WarehousingLog;
import com.framework.stock.service.IAllotWarehouseDetailService;
import com.framework.stock.service.IAllotWarehouseService;
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
public class AllotWarehouseServiceImpl extends ServiceImpl<AllotWarehouseMapper, AllotWarehouse>
		implements IAllotWarehouseService {

	@Autowired
	private IWarehouseService warehouseService;

	@Autowired
	private IStockService stockService;

	@Autowired
	private StockKeepingUnitMapper skuMapping;
	
	@Autowired
	private IAllotWarehouseDetailService allotDetailService;
	
	@Autowired
	private IWarehousingLogService warehousingLogService;
	
	@Autowired
	private IOutwarehouseLogService outwarehouseLogService;

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String applyAllotWarehouse(AllotWarehouse allotWarehouse) {
		log.info("[AllotWarehouseServiceApi] -> applyAllotWarehouse start");
		String allotWarehouseNo = OrderNoUtils.createAllotWarehouseNo();
		String outWarehouseId = allotWarehouse.getOutWarehouseId();
		String enterWarehouseId = allotWarehouse.getEnterWarehouseId();
		String applyUserId = allotWarehouse.getApplyUserId();
		Date applyTime = new Date();
		List<AllotWarehouseDetail> allotList = allotWarehouse.getAllotWarehouseDetailList();
		
		// 判断仓库
		if (StringUtils.isEmpty(outWarehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50118);
		}
		if (StringUtils.isEmpty(enterWarehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50018);
		}
		if (enterWarehouseId.equals(outWarehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50230);
		}
		if (warehouseService.getById(outWarehouseId) == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		if (warehouseService.getById(enterWarehouseId) == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		
		// 判断申请人
		if (StringUtils.isEmpty(applyUserId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50020);
		}
		// 判断调仓商品信息
		if (allotList.isEmpty()) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50223);
		}
		
		// 判断商品调仓等信息
		List<AllotWarehouseDetail> allotDetailList = new ArrayList<>(allotList.size());
		for (AllotWarehouseDetail detail : allotList) {
			String skuId = detail.getSkuId();
			Integer quantity = detail.getQuantity();

			if (StringUtils.isEmpty(skuId)) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50024);
			}
			if (skuMapping.selectById(skuId) == null) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50025);
			}
			if (quantity == null || quantity.intValue() <= 0) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50035);
			}
			detail = detail.toBuilder().id(SnowFlakeUtils.getId()).allocNo(allotWarehouseNo).build();
			allotDetailList.add(detail);
		}
		
		allotWarehouse = allotWarehouse.toBuilder().id(SnowFlakeUtils.getId()).status(APPLY_STATUS.APPLY.value())
				.allocNo(allotWarehouseNo).applyTime(applyTime).createTime(applyTime).createUser(applyUserId)
				.build();

		// 插入调仓申请与明细表
		this.save(allotWarehouse);
		allotDetailService.saveBatch(allotDetailList);
		
		log.info("[AllotWarehouseServiceApi] -> applyAllotWarehouse success");
		return Constants.APPLY_ALLOTWAREHOUSE_SUCCESS;
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String approveAllotWarehouse(String id, String status, String approveUserId, String reason)
			throws ServiceException {
		log.info("[AllotWarehouseServiceApi] -> approveAllotWarehouse start");
		Date finishedTime = new Date();
		String msg;

		// 判断入库id
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50026);
		}
		// 获取调仓单
		AllotWarehouse allotWarehouse = this.getById(id);
		if (allotWarehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50227);
		}
		if (!allotWarehouse.getStatus().equals(APPLY_STATUS.APPLY.value())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50228);
		}
		// 判断审批人
		if (StringUtils.isEmpty(approveUserId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50229);
		}
		// 获取仓库信息
		Warehouse outWarehouse = warehouseService.getById(allotWarehouse.getOutWarehouseId());
		if (outWarehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		String outWarehouseId = outWarehouse.getId();
		Warehouse enterWarehouse = warehouseService.getById(allotWarehouse.getEnterWarehouseId());
		if (enterWarehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		String enterWarehouseId = enterWarehouse.getId();
		

		AllotWarehouse entity = new AllotWarehouse().toBuilder().id(id).status(status).approveUserId(approveUserId)
				.modifyUser(approveUserId).modifyTime(finishedTime).reason(reason).finishedTime(finishedTime).build();
		// 判断状态,根据审批状态判断是否
		if (status.equals(APPLY_STATUS.PASS.value())) {
			msg = Constants.ALLOTWAREHOUSE_SUCCESS;
			// 查询调仓的商品
			LambdaQueryWrapper<AllotWarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
			detailQueryWrapper.eq(AllotWarehouseDetail::getAllocNo, allotWarehouse.getAllocNo());
			List<AllotWarehouseDetail> detailList = allotDetailService.list(detailQueryWrapper);
			// 调仓并记录出入库信息
			for (AllotWarehouseDetail detail : detailList) {
				String skuId = detail.getSkuId();
				Integer quantity = detail.getQuantity();

				StockKeepingUnit sku = skuMapping.selectById(skuId);
				if (sku == null) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50025);
				}
				
				// 先扣减库存，后新增库存
				String orderNo = allotWarehouse.getAllocNo();// 调仓单号作为orderNo
				if (!stockService.reduceStock(outWarehouseId, skuId, quantity, OUTWAREHOUSE_TYPE.ALLOT_OUT.value(), orderNo)) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50132.getCode(),
							StockServiceExceptionEnum.CODE_50132.getMessage() + skuId);
				}
				if (!stockService.addStock(enterWarehouseId, skuId, quantity, WAREHOUSING_TYPE.ALLOT_ENTER.value(), orderNo)) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50032.getCode(),
							StockServiceExceptionEnum.CODE_50032.getMessage() + skuId);
				}
				
				// 记录商品出入库记录
				OutwarehouseLog outLogEntity = new OutwarehouseLog().toBuilder()
						.warehouseId(outWarehouseId)
						.skuId(skuId)
						.skuName(sku.getName())
						.quantity(quantity)
						.type(OUTWAREHOUSE_TYPE.ALLOT_OUT.value())
						.createTime(finishedTime)
						.serialNo(orderNo).build();
				outwarehouseLogService.save(outLogEntity);
				WarehousingLog logEntity = new WarehousingLog().toBuilder()
						.warehouseId(enterWarehouseId)
						.skuId(skuId)
						.skuName(sku.getName())
						.quantity(quantity)
						.type(WAREHOUSING_TYPE.ALLOT_ENTER.value())
						.createTime(finishedTime)
						.serialNo(orderNo).build();
				warehousingLogService.save(logEntity);
			}
			this.updateById(entity);
		} else if (status.equals(APPLY_STATUS.REJECT.value())) {
			// 拒绝需要具体原因
			if (StringUtils.isEmpty(reason)) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50031);
			}
			msg = Constants.ALLOTWAREHOUSE_REJECT;
			this.updateById(entity);
		} else {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50030);
		}

		log.info("[AllotWarehouseServiceApi] -> approveAllotWarehouse success");
		return msg;
	}

	@Override
	public List<AllotWarehouse> queryAllotWarehouses(String allotNo) {
		// 查询列表
		LambdaQueryWrapper<AllotWarehouse> allotWarehouseQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(allotNo)) {
			allotWarehouseQueryWrapper.like(AllotWarehouse::getAllocNo, allotNo);
		}
		List<AllotWarehouse> allotWarehouseList = this.list(allotWarehouseQueryWrapper);
		List<String> allotNoList = allotWarehouseList.stream().map(AllotWarehouse::getAllocNo)
				.collect(Collectors.toList());

		// 查询明细
		LambdaQueryWrapper<AllotWarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.in(AllotWarehouseDetail::getAllocNo, allotNoList);
		List<AllotWarehouseDetail> detailList = allotDetailService.list(detailQueryWrapper);

		for (int i = 0; i < allotWarehouseList.size(); i++) {
			AllotWarehouse allotWarehouse = allotWarehouseList.get(i);
			String ANo = allotWarehouse.getAllocNo();

			List<AllotWarehouseDetail> adetailList = detailList.stream().filter(w -> w.getAllocNo().equals(ANo))
					.collect(Collectors.toList());

			allotWarehouse.setAllotWarehouseDetailList(adetailList);
		}
		return allotWarehouseList;
	}

	@Override
	public List<AllotWarehouse> getAllotWarehouse(String allotNo) {
		// 查询列表
		LambdaQueryWrapper<AllotWarehouse> allotWarehouseQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(allotNo)) {
			allotWarehouseQueryWrapper.like(AllotWarehouse::getAllocNo, allotNo);
		}
		List<AllotWarehouse> allotWarehouseList = this.list(allotWarehouseQueryWrapper);
		return allotWarehouseList;
	}

	@Override
	public List<AllotWarehouseDetail> getAllotWarehouseDetail(String allotNo) {
		if (StringUtils.isEmpty(allotNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50224);
		}
		// 查询明细
		LambdaQueryWrapper<AllotWarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.eq(AllotWarehouseDetail::getAllocNo, allotNo);
		List<AllotWarehouseDetail> detailList = allotDetailService.list(detailQueryWrapper);
		
		return detailList;
	}

}
