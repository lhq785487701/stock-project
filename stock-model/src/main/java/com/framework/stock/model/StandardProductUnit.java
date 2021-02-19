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
 * sku实体类
 * 
 * @author lihaoqi
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_stock_spu")
public class StandardProductUnit implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String catalog;
	
	// 类型显示名称
	private String name;

	// 固定属性列表，比如["产地":"中国", "重量":"200g"]
	private String fixedProperties;

	// 可变属性的可选项列表，比如["颜色":["红", "黄","蓝"], "内存":["128G", "256G"]]
	private String variableProperties;
	
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
