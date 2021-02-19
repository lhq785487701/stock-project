package com.framework.stock.api;

import java.util.List;

import com.framework.stock.common.response.ServiceResult;
import com.framework.stock.model.StandardProductUnit;
import com.framework.stock.model.StockKeepingUnit;

/**
 * @Description sku接口类
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface StockKeepingUnitServiceApi {
	
	/**
	 * @Description 创建sku
	 * @param skuList
	 * @return
	 */
	ServiceResult createSku(List<StockKeepingUnit> skuList);
	
	/**
	 * @Description 创建spu
	 * @param spuList
	 * @return
	 */
	ServiceResult createSpu(List<StandardProductUnit> spuList);

}
