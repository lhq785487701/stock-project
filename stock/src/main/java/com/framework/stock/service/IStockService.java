package com.framework.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.Stock;
/**
 * @Description 库存接口类
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface IStockService extends IService<Stock> {
	
	/**
	 * @Description 通过仓库id和商品skuid查询库存
	 * @param skuId
	 * @return
	 */
	int queryStockBySkuId(String warehouseId, String skuId);
	
	/**
	 * @Description 查询库存，模糊查询条件
	 * @param skuName
	 * @return
	 */
	List<Stock> queryStocks(String skuName);
	
	/**
	 * @Description 查询某个仓库内的库存记录
	 * @param warehouseId
	 * @return
	 */
	List<Stock> queryStocksByWarehouse(String warehouseId);
	
	/**
	 * @Description 加库存
	 * @param warehouseId
	 * @param skuId
	 * @param quantity
	 * @param changType
	 * @param orderNo 订单号(入/出库单号)
	 * @return
	 */
	boolean addStock(String warehouseId, String skuId, Integer quantity, String changType, String orderNo);

	/**
	 * @Description 扣减库存，扣减库存存在销售订单库存和非销售订单库存，销售库存存在以下情况：
	 * 				1.直接销售(直接使用可用库存出库)
	 * 				2.先锁定库存(使用预留库存出库)
	 * @param warehouseId
	 * @param skuId
	 * @param quantity
	 * @param changType
	 * @param orderNo
	 * @return
	 */
	boolean reduceStock(String warehouseId, String skuId, Integer quantity, String changType, String orderNo);
	

	/**
	 * @Description 锁定库存:
	 * 				1.业务系统上架商品时，锁定对应的可用库存作为预留库存
	 * 				2.业务系统没有自己预留库存，直接购买商品时，锁定库存，取消订单释放或支付订单出库
	 * @param warehouseId 仓库id
	 * @param skuId 商品id
	 * @param quantity 锁定数量
	 * @param orderNo 业务订单号
	 * @return
	 */
	String lockStock(String warehouseId, String skuId, Integer quantity, String orderNo);
	
	/**
	 * @Description 解锁库存
	 * @param warehouseId 仓库id
	 * @param skuId 商品id
	 * @param quantity 解锁数量
	 * @param orderNo 业务订单号
	 * @return
	 */
	String unlockStock(String warehouseId, String skuId, Integer quantity, String orderNo);
	
}
