package com.framework.stock.api;

import com.framework.stock.common.response.ServiceResult;

/**
 * @Description 库存接口类
 * @author lihaoqi
 * @date 2020年12月1日
 *
 */
public interface StockServiceApi {


	/**
	 * @Description 查询库存
	 * @param skuName
	 * @return
	 */
	ServiceResult queryStocks(String skuName);
	
	/**
	 * @Description 通过仓库id和商品skuid查询库存
	 * @param skuId
	 * @return
	 */
	ServiceResult queryStockBySkuId(String warehouseId, String skuId);

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
	ServiceResult lockStock(String warehouseId, String skuId, Integer quantity, String orderNo);
	
	/**
	 * @Description 解锁库存
	 * @param warehouseId 仓库id
	 * @param skuId 商品id
	 * @param quantity 解锁数量
	 * @param orderNo 业务订单号
	 * @return
	 */
	ServiceResult unlockStock(String warehouseId, String skuId, Integer quantity, String orderNo);
}
