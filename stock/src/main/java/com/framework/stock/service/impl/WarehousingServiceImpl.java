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
import com.framework.stock.constant.StockConstantEnum.WAREHOUSING_TYPE;
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.StockKeepingUnitMapper;
import com.framework.stock.mapper.WarehousingMapper;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.model.Warehouse;
import com.framework.stock.model.Warehousing;
import com.framework.stock.model.WarehousingDetail;
import com.framework.stock.model.WarehousingLog;
import com.framework.stock.service.IStockService;
import com.framework.stock.service.IWarehouseService;
import com.framework.stock.service.IWarehousingDetailService;
import com.framework.stock.service.IWarehousingLogService;
import com.framework.stock.service.IWarehousingService;
import com.framework.stock.utils.OrderNoUtils;
import com.framework.stock.utils.SnowFlakeUtils;
import com.framework.stock.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarehousingServiceImpl extends ServiceImpl<WarehousingMapper, Warehousing>
		implements IWarehousingService {

	@Autowired
	private IWarehouseService warehouseService;

	@Autowired
	private IStockService stockService;

	@Autowired
	private StockKeepingUnitMapper skuMapping;

	@Autowired
	private IWarehousingDetailService warehousingDetailService;

	@Autowired
	private IWarehousingLogService warehousingLogService;

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String applyWarehousing(Warehousing warehousing) {
		log.info("[WarehousingServiceApi] -> applyWarehousing start");
		String warehousingNo = OrderNoUtils.createWarehousingNo();
		String warehouseId = warehousing.getWarehouseId();
		String applyUserId = warehousing.getApplyUserId();
		String type = warehousing.getType();
		Date applyTime = new Date();
		List<WarehousingDetail> detailList = warehousing.getWarehousingDetailList();

		// 判断仓库
		if (StringUtils.isEmpty(warehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50018);
		}
		if (warehouseService.getById(warehouseId) == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		// 判断申请人
		if (StringUtils.isEmpty(applyUserId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50020);
		}
		// 判断入库类型
		if (StringUtils.isEmpty(type)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50021);
		}
		if (!WAREHOUSING_TYPE.containValue(type)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50022);
		}
		// 判断入库商品信息
		if (detailList.isEmpty()) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50023);
		}
		// 判断商品入库等信息
		List<WarehousingDetail> warehousingDetailList = new ArrayList<>(detailList.size());
		for (WarehousingDetail detail : detailList) {
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
			detail = detail.toBuilder().id(SnowFlakeUtils.getId()).warehousingNo(warehousingNo).build();
			warehousingDetailList.add(detail);
		}
		warehousing = warehousing.toBuilder().id(SnowFlakeUtils.getId()).status(APPLY_STATUS.APPLY.value())
				.warehousingNo(warehousingNo).applyTime(applyTime).createTime(applyTime).createUser(applyUserId)
				.build();

		// 插入入库申请与明细表
		this.save(warehousing);
		warehousingDetailService.saveBatch(warehousingDetailList);
		
		log.info("[WarehousingServiceApi] -> applyWarehousing success");
		return Constants.APPLY_WAREHOUSING_SUCCESS;
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String approveWarehousing(String id, String status, String approveUserId, String reason) {
		log.info("[WarehousingServiceApi] -> approveWarehousing start");
		Date finishedTime = new Date();
		String msg;

		// 判断入库id
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50026);
		}
		// 获取入库单
		Warehousing warehousing = this.getById(id);
		if (warehousing == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50027);
		}
		if (!warehousing.getStatus().equals(APPLY_STATUS.APPLY.value())) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50028);
		}
		// 获取入库类型
		String type = warehousing.getType();
		// 判断申请人
		if (StringUtils.isEmpty(approveUserId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50029);
		}
		// 获取仓库信息
		Warehouse warehouse = warehouseService.getById(warehousing.getWarehouseId());
		if (warehouse == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		String warehouseId = warehouse.getId();

		Warehousing entity = new Warehousing().toBuilder().id(id).status(status).approveUserId(approveUserId)
				.modifyUser(approveUserId).modifyTime(finishedTime).reason(reason).finishedTime(finishedTime).build();
		// 判断状态,根据审批状态判断是否
		if (status.equals(APPLY_STATUS.PASS.value())) {
			msg = Constants.WAREHOUSING_SUCCESS;
			// 查询入库表需入库的商品
			LambdaQueryWrapper<WarehousingDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
			detailQueryWrapper.eq(WarehousingDetail::getWarehousingNo, warehousing.getWarehousingNo());
			List<WarehousingDetail> detailList = warehousingDetailService.list(detailQueryWrapper);
			// 入库并记录入库信息
			for (WarehousingDetail detail : detailList) {
				String skuId = detail.getSkuId();
				Integer quantity = detail.getQuantity();

				StockKeepingUnit sku = skuMapping.selectById(skuId);
				if (sku == null) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50025);
				}

				// 增加商品库存,如果商品入库失败，则回滚操作
				if (!stockService.addStock(warehouseId, skuId, quantity, type, warehousing.getOrderNo())) {
					throw new ServiceException(StockServiceExceptionEnum.CODE_50032.getCode(),
							StockServiceExceptionEnum.CODE_50032.getMessage() + skuId);
				}
				
				// 记录商品入库记录
				WarehousingLog logEntity = new WarehousingLog().toBuilder()
						.warehouseId(warehouseId)
						.skuId(skuId)
						.skuName(sku.getName())
						.quantity(quantity)
						.type(type)
						.createTime(finishedTime)
						.serialNo(warehousing.getOrderNo()).build();
				warehousingLogService.save(logEntity);
			}
			// 更新单据信息
			this.updateById(entity);
		} else if (status.equals(APPLY_STATUS.REJECT.value())) {
			// 拒绝需要具体原因
			if (StringUtils.isEmpty(reason)) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50031);
			}
			msg = Constants.WAREHOUSING_REJECT;
			this.updateById(entity);
		} else {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50030);
		}
		log.info("[WarehousingServiceApi] -> approveWarehousing success");
		return msg;
	}

	@Override
	public List<Warehousing> queryWarehousings(String warehousingNo) {
		// 查询列表
		LambdaQueryWrapper<Warehousing> warehousingQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(warehousingNo)) {
			warehousingQueryWrapper.like(Warehousing::getWarehousingNo, warehousingNo);
		}
		List<Warehousing> warehousingList = this.list(warehousingQueryWrapper);
		List<String> warehousingNoList = warehousingList.stream().map(Warehousing::getWarehousingNo)
				.collect(Collectors.toList());

		// 查询明细
		LambdaQueryWrapper<WarehousingDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.in(WarehousingDetail::getWarehousingNo, warehousingNoList);
		List<WarehousingDetail> detailList = warehousingDetailService.list(detailQueryWrapper);

		for (int i = 0; i < warehousingList.size(); i++) {
			Warehousing warehousing = warehousingList.get(i);
			String wNo = warehousing.getWarehousingNo();

			List<WarehousingDetail> wdetailList = detailList.stream().filter(w -> w.getWarehousingNo().equals(wNo))
					.collect(Collectors.toList());

			warehousing.setWarehousingDetailList(wdetailList);
		}
		return warehousingList;
	}

	@Override
	public List<Warehousing> getWarehousing(String warehousingNo) {
		// 查询列表
		LambdaQueryWrapper<Warehousing> warehousingQueryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(warehousingNo)) {
			warehousingQueryWrapper.like(Warehousing::getWarehousingNo, warehousingNo);
		}
		List<Warehousing> warehousingList = this.list(warehousingQueryWrapper);

		return warehousingList;
	}

	@Override
	public List<WarehousingDetail> getWarehousingDetail(String warehousingNo) {
		if (StringUtils.isEmpty(warehousingNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50034);
		}
		// 查询明细
		LambdaQueryWrapper<WarehousingDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
		detailQueryWrapper.eq(WarehousingDetail::getWarehousingNo, warehousingNo);
		List<WarehousingDetail> detailList = warehousingDetailService.list(detailQueryWrapper);
		
		return detailList;
	}

}
