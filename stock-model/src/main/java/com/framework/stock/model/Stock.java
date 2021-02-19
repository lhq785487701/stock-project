package com.framework.stock.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 供应商实体类
 * 
 * @author lihaoqi
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_stock")
public class Stock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String warehouseId;
	
	private String skuId;
	
	private Integer totalQuantity;
	
	private Integer availableQuantity;
	
	private Integer reservedQuantity;
	
	private String catalogName;  // SKU所属类型显示名称 iphone6手机
	
	private String name; // SKU显示名称 iphone6 红色/128G
	
	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private Date createTime;
	
	private Date modifyTime;
	
	@TableField(value = "is_enabled",exist = true)
	private boolean enabled;

	@TableField(value = "is_deleted",exist = true)
	private boolean deleted;
	
	private String remark;
}

