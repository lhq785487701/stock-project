package com.framework.stock.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.StandardProductUnitMapper;
import com.framework.stock.mapper.StockKeepingUnitMapper;
import com.framework.stock.model.StandardProductUnit;
import com.framework.stock.model.StockKeepingUnit;
import com.framework.stock.service.IStockKeepingUnitService;
import com.framework.stock.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description sku
 * @author lihaoqi
 * @date 2020年12月2日
 *
 */
@Service
@Slf4j
public class StockKeepingUnitServiceImpl extends ServiceImpl<StockKeepingUnitMapper, StockKeepingUnit>
		implements IStockKeepingUnitService {
	
	@Autowired
	private StandardProductUnitMapper spuMapping;

	@Override
	public StockKeepingUnit getSkuAndSpuBySkuId(String skuId) {
		return this.getBaseMapper().getSkuAndSpuBySkuId(skuId);
	}

	@Override
	public Map<String, Object> createSku(List<StockKeepingUnit> skuList) {
		Map<String, Object> result = new HashMap<>(2);
		List<String> skuSuccessList = new ArrayList<>();
		List<String> skuErrorList = new ArrayList<>();
		List<StockKeepingUnit> skus = new ArrayList<>(skuList.size());
		
		if (!skuList.isEmpty()) {
			for (StockKeepingUnit sku : skuList) {
				String skuId = sku.getId();
				String spuId = sku.getSpuId();
				if (StringUtils.isEmpty(skuId)) {
					log.error("skuId 为空");
					skuErrorList.add("none");
					continue;
				}
				if (this.getById(skuId) != null) {
					log.error("skuId 重复");
					skuErrorList.add(skuId);
					continue;
				}
				
				if (StringUtils.isEmpty(spuId)) {
					log.error("spuId 为空");
					skuErrorList.add(skuId);
					continue;
				}
				if (spuMapping.selectById(spuId) == null) {
					log.error("spuId 不存在");
					skuErrorList.add(skuId);
					continue;
				}
				if (StringUtils.isEmpty(sku.getName())) {
					log.error("name 为空");
					skuErrorList.add(skuId);
					continue;
				}
				if (StringUtils.isEmpty(sku.getProperties())) {
					log.error("商品属性为空");
					skuErrorList.add(skuId);
					continue;
				}
				skuSuccessList.add(spuId);
				skus.add(sku.toBuilder().createTime(new Date()).build());
			}
		}
		if(!skus.isEmpty()) {
			this.saveBatch(skus);
		}
		
		result.put("successList", skuSuccessList);
		result.put("errorList", skuErrorList);
		return result;
	}

	@Override
	public Map<String, Object> createSpu(List<StandardProductUnit> spuList) {
		Map<String, Object> result = new HashMap<>(2);
		List<String> spuSuccessList = new ArrayList<>();
		List<String> spuErrorList = new ArrayList<>();
		
		if (!spuList.isEmpty()) {
			for (StandardProductUnit spu : spuList) {
				String spuId = spu.getId();
				if (StringUtils.isEmpty(spuId)) {
					log.error("spuId 为空");
					spuErrorList.add("none");
					continue;
				}
				if (spuMapping.selectById(spuId) != null) {
					log.error("spuId 重复");
					spuErrorList.add(spuId);
					continue;
				}
				if (StringUtils.isEmpty(spu.getCatalog())) {
					log.error("catalog 为空");
					spuErrorList.add(spuId);
					continue;
				}
				if (StringUtils.isEmpty(spu.getName())) {
					log.error("name 为空");
					spuErrorList.add(spuId);
					continue;
				}
				spuSuccessList.add(spuId);
				spuMapping.insert(spu.toBuilder().createTime(new Date()).build());
			}
		}
		result.put("successList", spuSuccessList);
		result.put("errorList", spuErrorList);
		return result;
	}

}
