package com.framework.stock.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 库存日志
 * @author lihaoqi
 * @date 2020年12月3日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_stock_change_log")
public class StockChangeLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	
	private String stockId;
	
	private String skuId;
	
	private String changeType;
	
	private Integer totalQuantity;

	private Integer availableQuantity;
	
	private Integer reservedQuantity;
	
	private Integer changeQuantity;
	
	private Date changeTime;
	
	private String orderNo;
	
	private String remark;
}

