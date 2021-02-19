package com.framework.stock.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.common.exception.ServiceException;
import com.framework.stock.common.service.RedisService;
import com.framework.stock.constant.Constants;
import com.framework.stock.constant.StockConstantEnum.OUTWAREHOUSE_TYPE;
import com.framework.stock.constant.StockConstantEnum.STOCK_CHANGE_TYPE;
import com.framework.stock.exception.StockServiceExceptionEnum;
import com.framework.stock.mapper.StockMapper;
import com.framework.stock.model.Stock;
import com.framework.stock.model.StockChangeLog;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.service.IStockChangeLogService;
import com.framework.stock.service.IStockKeepingUnitService;
import com.framework.stock.service.IStockService;
import com.framework.stock.service.IWarehouseService;
import com.framework.stock.utils.SnowFlakeUtils;
import com.framework.stock.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 库存
 * @author lihaoqi
 * @date 2020年12月2日 
 *
 */
@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

	@Autowired
	private RedisService redisService;
	
	@Autowired
	private IStockChangeLogService stockLogService;
	
	@Autowired
	private IStockKeepingUnitService skuService;
	
	@Autowired
	private IWarehouseService warehouseService;

	@Override
	public List<Stock> queryStocks(String skuName) {

		LambdaQueryWrapper<Stock> queryWrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotEmpty(skuName)) {
			queryWrapper.like(Stock::getName, skuName);
		}
		return this.getBaseMapper().selectList(queryWrapper);
	}
	
	@Override
	public List<Stock> queryStocksByWarehouse(String warehouseId) {
		if (StringUtils.isEmpty(warehouseId)) {
			return new ArrayList<>();
		}
		
		LambdaQueryWrapper<Stock> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(Stock::getWarehouseId, warehouseId);
		return this.getBaseMapper().selectList(queryWrapper);
	}
	
	@Override
	public int queryStockBySkuId(String warehouseId, String skuId) {
		int availableQuantity = 0;
		String key = Constants.STOCK_AVAILABLE_QUANTITY_REDIS_PREFIX + warehouseId + ":" + skuId;
		
		if (StringUtils.isEmpty(skuId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50001);
		}

		Object availableQuantityObj = redisService.get(key);
		if (availableQuantityObj == null) {
			LambdaQueryWrapper<Stock> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(Stock::getSkuId, skuId);
			queryWrapper.eq(Stock::getWarehouseId, warehouseId);
			Stock stock = this.getOne(queryWrapper);
			if (stock != null) {
				availableQuantity = stock.getAvailableQuantity();
				redisService.set(key, availableQuantity);
			}
		} else {
			availableQuantity = (int) availableQuantityObj;
		}
		return availableQuantity;
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String lockStock(String warehouseId, String skuId, Integer quantity, String orderNo) {
		if (StringUtils.isEmpty(warehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50007);
		}
		if (warehouseService.getById(warehouseId) == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		if (StringUtils.isEmpty(skuId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50024);
		}
		if (quantity == null || quantity <= 0) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50035);
		}
		if (StringUtils.isEmpty(orderNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50038);
		}
		
		String key = Constants.STOCK_REDISNX_LOCK + warehouseId + ":" + skuId;
		try {
			if (!redisService.tryLock(key, Constants.STOCK_REDISNX_TIMEOUT)) {
				log.error("无法获取分布式锁");
				throw new ServiceException(StockServiceExceptionEnum.CODE_500);
			}
			Date stockTime = new Date();
			
			LambdaQueryWrapper<Stock> stockQueryWrapper = new LambdaQueryWrapper<>();
			stockQueryWrapper.eq(Stock::getSkuId, skuId);
			stockQueryWrapper.eq(Stock::getWarehouseId, warehouseId);
			Stock stock = this.getOne(stockQueryWrapper);
			// 判断该商品的库存是否存在
			if (stock == null) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50037);
			}
			// 判断该业务订单号是否已经锁定了库存，不允许重复锁定也不允许同一销售订单多次锁定
			LambdaQueryWrapper<StockChangeLog> lockQueryWrapper = new LambdaQueryWrapper<>();
			lockQueryWrapper.eq(StockChangeLog::getStockId, stock.getId());
			lockQueryWrapper.eq(StockChangeLog::getChangeType, STOCK_CHANGE_TYPE.LOCKSTOCK.value());
			lockQueryWrapper.eq(StockChangeLog::getOrderNo, orderNo);
			int lockStock = stockLogService.count(lockQueryWrapper);
			if (lockStock >= 1) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50041);
			}
			// 判断可用库存是否足够
			if (stock.getAvailableQuantity() - quantity < 0) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50135);
			}
			stock = stock.toBuilder()
					.availableQuantity(stock.getAvailableQuantity() - quantity)
					.reservedQuantity(stock.getReservedQuantity() + quantity)
					.modifyTime(stockTime).build();
			this.updateById(stock);
			// 改变库存数量，删除库存缓存信息
			redisService.remove(Constants.STOCK_AVAILABLE_QUANTITY_REDIS_PREFIX + skuId);
			// 记录库存变化日志
			StockChangeLog stockLogEntity = new StockChangeLog().toBuilder()
					.stockId(stock.getId())
					.skuId(skuId)
					.changeType(STOCK_CHANGE_TYPE.LOCKSTOCK.value())
					.changeQuantity(quantity)
					.availableQuantity(stock.getAvailableQuantity())
					.totalQuantity(stock.getTotalQuantity())
					.reservedQuantity(stock.getReservedQuantity())
					.changeTime(stockTime)
					.orderNo(orderNo).build();
			stockLogService.save(stockLogEntity);
			return Constants.LOCKSTOCK_SUCCESS;
		} catch (ServiceException e) {
			log.error("入库失败，错误：", e);
			throw new ServiceException(e);
		} catch (Exception e) {
			log.error("系统错误：", e);
			throw new ServiceException(StockServiceExceptionEnum.CODE_500);
		} finally {
			redisService.unlock(key);
		}
	}
	
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public String unlockStock(String warehouseId, String skuId, Integer quantity, String orderNo) {
		if (StringUtils.isEmpty(orderNo)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50038);
		}
		if (StringUtils.isEmpty(warehouseId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50007);
		}
		if (warehouseService.getById(warehouseId) == null) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50033);
		}
		if (StringUtils.isEmpty(skuId)) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50024);
		}
		
		String key = Constants.STOCK_REDISNX_LOCK + warehouseId + ":" + skuId;
		try {
			if (!redisService.tryLock(key, Constants.STOCK_REDISNX_TIMEOUT)) {
				log.error("无法获取分布式锁");
				throw new ServiceException(StockServiceExceptionEnum.CODE_500);
			}
			Date stockTime = new Date();
			
			// 判断该商品的库存是否存在
			LambdaQueryWrapper<Stock> stockQueryWrapper = new LambdaQueryWrapper<>();
			stockQueryWrapper.eq(Stock::getSkuId, skuId);
			stockQueryWrapper.eq(Stock::getWarehouseId, warehouseId);
			Stock stock = this.getOne(stockQueryWrapper);
			if (stock == null) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50037);
			}
			// 统计该业务订单号下该商品
			LambdaQueryWrapper<StockChangeLog> lockQueryWrapper = new LambdaQueryWrapper<>();
			lockQueryWrapper.eq(StockChangeLog::getStockId, stock.getId());
			lockQueryWrapper.eq(StockChangeLog::getOrderNo, orderNo);
			List<StockChangeLog> lockList = stockLogService.list(lockQueryWrapper);
			// 获取业务订单下的锁定库存和解锁库存的条数
			long lockStockCount = lockList.stream()
					.filter(c -> c.getChangeType().equals(STOCK_CHANGE_TYPE.LOCKSTOCK.value())).count();
			if (lockStockCount < 1) {
				log.error("该业务订单不存在锁定库存：{}", orderNo);
				throw new ServiceException(StockServiceExceptionEnum.CODE_50042);
			}
			// 获取可解锁库存 = 锁定库存的数量 - 已释放库存的数量
			int releasableQuantity = lockList.stream().filter(c -> c.getChangeType().equals(STOCK_CHANGE_TYPE.LOCKSTOCK.value()))
			.map(StockChangeLog::getChangeQuantity).reduce(0, Integer::sum) - 
			lockList.stream().filter(c -> c.getChangeType().equals(STOCK_CHANGE_TYPE.UNLOCKSTOCK.value())
					|| c.getChangeType().equals(OUTWAREHOUSE_TYPE.SALE_ORDER_RESERVED.value()))
			.map(StockChangeLog::getChangeQuantity).reduce(0, Integer::sum);
			if (releasableQuantity <= 0 || quantity > releasableQuantity) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50039);
			}
			
			// 判断预留库存数量
			if (stock.getReservedQuantity() - quantity < 0) {
				throw new ServiceException(StockServiceExceptionEnum.CODE_50040);
			}
			stock = stock.toBuilder()
					.availableQuantity(stock.getAvailableQuantity() + quantity)
					.reservedQuantity(stock.getReservedQuantity() - quantity)
					.modifyTime(stockTime).build();
			this.updateById(stock);
			// 改变库存数量，删除库存缓存信息
			redisService.remove(Constants.STOCK_AVAILABLE_QUANTITY_REDIS_PREFIX + skuId);
			// 记录库存变化日志
			StockChangeLog stockLogEntity = new StockChangeLog().toBuilder()
					.stockId(stock.getId())
					.skuId(skuId)
					.changeType(STOCK_CHANGE_TYPE.UNLOCKSTOCK.value())
					.changeQuantity(quantity)
					.availableQuantity(stock.getAvailableQuantity())
					.totalQuantity(stock.getTotalQuantity())
					.reservedQuantity(stock.getReservedQuantity())
					.changeTime(stockTime)
					.orderNo(orderNo).build();
			stockLogService.save(stockLogEntity);
			return Constants.UNLOCKSTOCK_SUCCESS;
		} catch (ServiceException e) {
			log.error("解锁失败，错误：", e);
			throw new ServiceException(e);
		} catch (Exception e) {
			log.error("解锁失败，错误：", e);
			throw new ServiceException(StockServiceExceptionEnum.CODE_500);
		} finally {
			redisService.unlock(key);
		}
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public boolean addStock(String warehouseId, String skuId, Integer quantity, String changType, String orderNo) {
		if (quantity <= 0) {
			log.error("入库数量小于0");
			throw new ServiceException(StockServiceExceptionEnum.CODE_50035);
		}
		String key = Constants.STOCK_REDISNX_LOCK + warehouseId + ":" + skuId;
		try {
			if (!redisService.tryLock(key, Constants.STOCK_REDISNX_TIMEOUT)) {
				log.error("无法获取分布式锁");
				throw new ServiceException(StockServiceExceptionEnum.CODE_50036);
			}
			Date stockTime = new Date();
			String remark = "";
			
			LambdaQueryWrapper<Stock> stockQueryWrapper = new LambdaQueryWrapper<>();
			stockQueryWrapper.eq(Stock::getSkuId, skuId);
			stockQueryWrapper.eq(Stock::getWarehouseId, warehouseId);
			Stock stock = this.getOne(stockQueryWrapper);
			// 判断该商品的库存是否存在，不存在则创建库存，存在则更新库存
			if (stock == null) {
				StockKeepingUnit skuMsg = skuService.getSkuAndSpuBySkuId(skuId);
				if (skuMsg == null ) {
					log.error("无法获取sku信息");
					throw new ServiceException(StockServiceExceptionEnum.CODE_50025);
				}
				
				stock = new Stock().toBuilder()
						.id(SnowFlakeUtils.getId())
						.skuId(skuId)
						.warehouseId(warehouseId)
						.catalogName(skuMsg.getCatalogName())
						.name(skuMsg.getName())
						.createTime(stockTime)
						.reservedQuantity(new Integer(0))
						.totalQuantity(quantity)
						.availableQuantity(quantity).build();
				this.save(stock);
				remark = "新建库存";
			} else {
				stock = stock.toBuilder()
						.totalQuantity(stock.getTotalQuantity() + quantity)
						.availableQuantity(stock.getAvailableQuantity() + quantity)
						.modifyTime(stockTime).build();
				this.updateById(stock);
				// 改变库存数量，删除库存缓存信息
				redisService.remove(Constants.STOCK_AVAILABLE_QUANTITY_REDIS_PREFIX + skuId);
			}
			// 记录库存变化日志
			StockChangeLog stockLogEntity = new StockChangeLog().toBuilder()
					.stockId(stock.getId())
					.skuId(skuId)
					.changeType(changType)
					.changeQuantity(quantity)
					.availableQuantity(stock.getAvailableQuantity())
					.totalQuantity(stock.getTotalQuantity())
					.reservedQuantity(stock.getReservedQuantity())
					.changeTime(stockTime)
					.remark(remark)
					.orderNo(orderNo).build();
			stockLogService.save(stockLogEntity);
			return true;
		} catch (ServiceException e) {
			log.error("入库失败，错误：", e);
			throw new ServiceException(e);
		} catch (Exception e) {
			log.error("入库失败，错误：", e);
			throw new ServiceException(StockServiceExceptionEnum.CODE_500);
		} finally {
			redisService.unlock(key);
		}
	}

	@Override
	public boolean reduceStock(String warehouseId, String skuId, Integer quantity, String changType,
			String orderNo) {
		if (quantity <= 0) {
			log.error("出库数量小于0");
			throw new ServiceException(StockServiceExceptionEnum.CODE_50035);
		}
		String key = Constants.STOCK_REDISNX_LOCK + warehouseId + ":" + skuId;
		try {
			if (!redisService.tryLock(key, Constants.STOCK_REDISNX_TIMEOUT)) {
				log.error("无法获取分布式锁");
				throw new ServiceException(StockServiceExceptionEnum.CODE_50138);
			}
			Date stockTime = new Date();
			String remark = "";
			String stockKey = Constants.STOCK_AVAILABLE_QUANTITY_REDIS_PREFIX + warehouseId + ":" + skuId;
			
			LambdaQueryWrapper<Stock> stockQueryWrapper = new LambdaQueryWrapper<>();
			stockQueryWrapper.eq(Stock::getSkuId, skuId);
			stockQueryWrapper.eq(Stock::getWarehouseId, warehouseId);
			Stock stock = this.getOne(stockQueryWrapper);
			// 判断该商品的库存是否存在，返回false，存在则更新库存
			if (stock == null) {
				log.error("仓库Id: {},没有skuId:{}的库存，无法扣减，请核对商品库存", warehouseId, skuId);
				throw new ServiceException(StockServiceExceptionEnum.CODE_50123);
			}
			// 判断是否使用锁定库存,并获取Stock对象
			boolean useReservedStock = isUseReservedStock(stock, quantity, orderNo);
			if (useReservedStock) {
				if (stock.getReservedQuantity() < quantity) {
					log.error("释放的库存超出该商品总锁定库存");
					throw new ServiceException(StockServiceExceptionEnum.CODE_50137);
				}
				stock = stock.toBuilder()
						.totalQuantity(stock.getTotalQuantity() - quantity)
						.reservedQuantity(stock.getReservedQuantity() - quantity)
						.modifyTime(stockTime).build();
				changType = OUTWAREHOUSE_TYPE.SALE_ORDER_RESERVED.value();
			} else {
				if (stock.getAvailableQuantity() - quantity < 0) {
					log.error("可用库存不足，无法扣减");
					throw new ServiceException(StockServiceExceptionEnum.CODE_50135);
				}
				stock = stock.toBuilder()
						.totalQuantity(stock.getTotalQuantity() - quantity)
						.availableQuantity(stock.getAvailableQuantity() - quantity)
						.modifyTime(stockTime).build();
			}
			
			// 更新库存
			this.updateById(stock);
			// 改变库存数量，删除库存缓存信息
			redisService.remove(stockKey);
			// 记录库存变化日志
			StockChangeLog stockLogEntity = new StockChangeLog().toBuilder()
					.stockId(stock.getId())
					.skuId(skuId)
					.changeType(changType)
					.changeQuantity(quantity)
					.availableQuantity(stock.getAvailableQuantity())
					.totalQuantity(stock.getTotalQuantity())
					.reservedQuantity(stock.getReservedQuantity())
					.changeTime(stockTime)
					.remark(remark)
					.orderNo(orderNo).build();
			stockLogService.save(stockLogEntity);
			return true;
		} catch (Exception e) {
			log.error("出库失败，错误：", e);
			throw new ServiceException(e);
		} finally {
			redisService.unlock(key);
		}
	}

	/**
	 * @Description 是否使用预留库存
	 * @param stock
	 * @param quantity
	 * @param stockTime
	 * @return
	 */
	private boolean isUseReservedStock(Stock stock, Integer quantity, String orderNo) {
		// 判断是否存在锁定的库存
		LambdaQueryWrapper<StockChangeLog> lockQueryWrapper = new LambdaQueryWrapper<>();
		lockQueryWrapper.eq(StockChangeLog::getStockId, stock.getId());
		lockQueryWrapper.eq(StockChangeLog::getOrderNo, orderNo);
		List<StockChangeLog> lockList = stockLogService.list(lockQueryWrapper);
		// 如果该订单没有保留库存需要释放，直接使用可用库存
		if (lockList == null || lockList.isEmpty()) {
			log.debug("该业务订单不存在锁定库存，直接使用可用库存");
			return false;
		}
		// 获取业务订单下的锁定库存和解锁库存的条数
		long lockStockCount = lockList.stream()
				.filter(c -> c.getChangeType().equals(STOCK_CHANGE_TYPE.LOCKSTOCK.value())).count();
		if (lockStockCount < 1) {
			log.debug("该业务订单不存在锁定库存，直接使用可用库存");
			return false;
		}
		// 获取可解锁库存 = 锁定库存的数量 - 已释放库存的数量
		int releasableQuantity = lockList.stream().filter(c -> c.getChangeType().equals(STOCK_CHANGE_TYPE.LOCKSTOCK.value()))
		.map(StockChangeLog::getChangeQuantity).reduce(0, Integer::sum) - 
		lockList.stream().filter(c -> c.getChangeType().equals(STOCK_CHANGE_TYPE.UNLOCKSTOCK.value()))
		.map(StockChangeLog::getChangeQuantity).reduce(0, Integer::sum);
		if (releasableQuantity <= 0 || quantity > releasableQuantity) {
			throw new ServiceException(StockServiceExceptionEnum.CODE_50039);
		}
		return true;		
	}


}
