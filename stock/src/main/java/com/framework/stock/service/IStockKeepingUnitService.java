package com.framework.stock.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.framework.stock.model.StandardProductUnit;
import com.framework.stock.model.StockKeepingUnit;
/**
 * @Description sku接口类
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface IStockKeepingUnitService extends IService<StockKeepingUnit> {

	/**
	 * @Description 通过skuid查询sku与spu信息
	 * @param skuId
	 * @return
	 */
	StockKeepingUnit getSkuAndSpuBySkuId(String skuId);
	
	/**
	 * @Description 创建sku
	 * @param skuList
	 * @return
	 */
	Map<String, Object> createSku(List<StockKeepingUnit> skuList);

	/**
	 * @Description 创建spu
	 * @param spuList
	 * @return
	 */
	Map<String, Object> createSpu(List<StandardProductUnit> spuList);
	
}
