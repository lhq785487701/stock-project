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
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.OutwarehouseMapper;
import com.framework.stock.mapper.StockKeepingUnitMapper;
import com.framework.stock.model.Outwarehouse;
import com.framework.stock.model.OutwarehouseDetail;
import com.framework.stock.model.OutwarehouseLog;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.model.Warehouse;
import com.framework.stock.service.IOutwarehouseDetailService;
import com.framework.stock.service.IOutwarehouseLogService;
import com.framework.stock.service.IOutwarehouseService;
import com.framework.stock.service.IStockService;
import com.framework.stock.service.IWarehouseService;
import com.framework.stock.utils.OrderNoUtils;
import com.framework.stock.utils.SnowFlakeUtils;
import com.framework.stock.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutwarehouseServiceImpl extends ServiceImpl<OutwarehouseMapper, Outwarehouse>
		implements IOutwarehouseService {

	@Autowired
	private IWarehouseService warehouseService;

	@Autowired
	private IStockService stockService;

	@Autowired
	private StockKeepingUnitMapper skuMapping;

	@Autowired
	private IOutwarehouseDetailService outwarehouseDetailService;

	@Autowired
	private IOutwarehouseLogService outwarehouseLogService;

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String applyOutwarehouse(Outwarehouse outwarehouse) {
		log.info("[OutwarehouseServiceApi] -> applyOutwarehouse start");
		String outwarehouseNo = OrderNoUtils.createOutwarehouseNo();
		String warehouseId = outwarehouse.getWarehouseId();
		String applyUserId = outwarehouse.getApplyUserId();
		String type = outwarehouse.getType();
		String orderNo = outwarehouse.getOrderNo();
		Date applyTime = new Date();
		String outwarehouseId = SnowFlakeUtils.getId();
		List<OutwarehouseDetail> detailList = outwarehouse.getOutwarehouseDetailList();

		// 判断仓库
		if (StringUtils.isEmpty(warehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50118);
		}
		Warehouse warehouse = warehouseService.getById(warehouseId);
		if (warehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		// 判断申请人
		if (StringUtils.isEmpty(applyUserId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50120);
		}
		// 判断出库类型
		if (StringUtils.isEmpty(type)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50121);
		}
		if (!OUTWAREHOUSE_TYPE.containValue(type)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50122);
		}
		// 判断订单号
		if (OUTWAREHOUSE_TYPE.SALE_ORDER.value().equals(type) && StringUtils.isEmpty(orderNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50136);
		}
		// 判断出库商品信息
		if (detailList.isEmpty()) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50123);
		}
		// 判断商品出库等信息
		List<OutwarehouseDetail> outwarehouseDetailList = new ArrayList<>(detailList.size());
		for (OutwarehouseDetail detail : detailList) {
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
			// 排除销售订单，因为销售订单存在预扣库存(锁定库存)
			if (!OUTWAREHOUSE_TYPE.SALE_ORDER.value().equals(type) && 
					quantity.intValue() > stockService.queryStockBySkuId(warehouse.getId(), skuId)) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50135);
			}
			
			detail = detail.toBuilder()
					.id(SnowFlakeUtils.getId())
					.outwarehouseNo(outwarehouseNo).build();
			outwarehouseDetailList.add(detail);
		}
		
		outwarehouse = outwarehouse.toBuilder()
				.id(outwarehouseId)
				.status(APPLY_STATUS.APPLY.value())
				.outwarehouseNo(outwarehouseNo)
				.orderNo(orderNo)
				.applyTime(applyTime)
				.createTime(applyTime)
				.createUser(applyUserId)
				.build();

		// 插入出库申请与明细表
		this.save(outwarehouse);
		outwarehouseDetailService.saveBatch(outwarehouseDetailList);
		
		// 销售订单的自动审批出库
		if (OUTWAREHOUSE_TYPE.SALE_ORDER.value().equals(type)) {
			return approveOutwarehouse(outwarehouseId, APPLY_STATUS.PASS.value(), Constants.AUTO_APPROVE_USER, null);
		} else {
			log.info("[OutwarehouseServiceApi] -> applyOutwarehouse success");
			return Constants.APPLY_OUTWAREHOUSE_SUCCESS;
		}
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String approveOutwarehouse(String id, String status, String approveUserId, String reason) {
		log.info("[OutwarehouseServiceApi] -> approveOutwarehouse start");
		Date finishedTime = new Date();
		String msg;

		// 判断出库id
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50126);
		}
		// 获取出库单
		Outwarehouse outwarehouse = this.getById(id);
		if (outwarehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50127);
		}
		if (!outwarehouse.getStatus().equals(APPLY_STATUS.APPLY.value())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50128);
		}
		// 获取出库类型
		String type = outwarehouse.getType();
		// 判断申请人
		if (StringUtils.isEmpty(approveUserId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50129);
		}
		// 获取仓库信息
		Warehouse warehouse = warehouseService.getById(outwarehouse.getWarehouseId());
		if (warehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		String warehouseId = warehouse.getId();

		Outwarehouse entity = new Outwarehouse().toBuilder().id(id).status(status).approveUserId(approveUserId)
				.modifyUser(approveUserId).modifyTime(finishedTime).reason(reason).finishedTime(finishedTime).build();
		// 判断状态,根据审批状态判断是否
		if (status.equals(APPLY_STATUS.PASS.value())) {
			msg = Constants.OUTWAREHOUSE_SUCCESS;
			this.updateById(entity);
			// 查询出库表需出库的商品
			LambdaQueryWrapper<OutwarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
			detailQueryWrapper.eq(OutwarehouseDetail::getOutwarehouseNo, outwarehouse.getOutwarehouseNo());
			List<OutwarehouseDetail> detailList = outwarehouseDetailService.list(detailQueryWrapper);
			// 出库并记录出库信息
			for (OutwarehouseDetail detail : detailList) {
				String skuId = detail.getSkuId();
				Integer quantity = detail.getQuantity();

				StockKeepingUnit sku = skuMapping.selectById(skuId);
				if (sku == null) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50025);
				}

				// 增加商品库存,如果商品出库失败，则回滚操作
				if (!stockService.reduceStock(warehouseId, skuId, quantity, type, outwarehouse.getOrderNo())) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50132.getCode(),
							StockServiceExceptionEnum.CODE_50132.getMessage() + skuId);
				}
				// 记录商品出库记录
				OutwarehouseLog logEntity = new OutwarehouseLog().toBuilder()
						.warehouseId(warehouseId)
						.skuId(skuId)
						.skuName(sku.getName())
						.quantity(quantity)
						.type(type)
						.createTime(finishedTime)
						.serialNo(outwarehouse.getOrderNo()).build();
				outwarehouseLogService.save(logEntity);
			}
		} else if (status.equals(APPLY_STATUS.REJECT.value())) {
			// 拒绝需要具体原因
			if (StringUtils.isEmpty(reason)) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50031);
			}
			msg = Constants.OUTWAREHOUSE_REJECT;
			this.updateById(entity);
		} else {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50030);
		}
		log.info("[OutwarehouseServiceApi] -> approveOutwarehouse success");
		return msg;
	}

	@Override
	public List<Outwarehouse> queryOutwarehouses(String outwarehouseNo) {

		// 查询列表
		LambdaQueryWrapper<Outwarehouse> outwarehouseQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(outwarehouseNo)) {
			outwarehouseQueryWrapper.like(Outwarehouse::getOutwarehouseNo, outwarehouseNo);
		}
		List<Outwarehouse> outwarehouseList = this.list(outwarehouseQueryWrapper);
		List<String> outwarehouseNoList = outwarehouseList.stream().map(Outwarehouse::getOutwarehouseNo)
				.collect(Collectors.toList());

		// 查询明细
		LambdaQueryWrapper<OutwarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.in(OutwarehouseDetail::getOutwarehouseNo, outwarehouseNoList);
		List<OutwarehouseDetail> detailList = outwarehouseDetailService.list(detailQueryWrapper);

		for (int i = 0; i < outwarehouseList.size(); i++) {
			Outwarehouse outwarehouse = outwarehouseList.get(i);
			String oNo = outwarehouse.getOutwarehouseNo();

			List<OutwarehouseDetail> wdetailList = detailList.stream().filter(w -> w.getOutwarehouseNo().equals(oNo))
					.collect(Collectors.toList());

			outwarehouse.setOutwarehouseDetailList(wdetailList);
		}
		return outwarehouseList;
	}

	@Override
	public List<Outwarehouse> getOutwarehouse(String outwarehouseNo) {
		// 查询列表
		LambdaQueryWrapper<Outwarehouse> outwarehouseQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(outwarehouseNo)) {
			outwarehouseQueryWrapper.like(Outwarehouse::getOutwarehouseNo, outwarehouseNo);
		}
		List<Outwarehouse> outwarehouseList = this.list(outwarehouseQueryWrapper);

		return outwarehouseList;
	}

	@Override
	public List<OutwarehouseDetail> getOutwarehouseDetail(String outwarehouseNo) {
		if (StringUtils.isEmpty(outwarehouseNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50134);
		}
		// 查询明细
		LambdaQueryWrapper<OutwarehouseDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.eq(OutwarehouseDetail::getOutwarehouseNo, outwarehouseNo);
		List<OutwarehouseDetail> detailList = outwarehouseDetailService.list(detailQueryWrapper);
		
		return detailList;
	}

}
