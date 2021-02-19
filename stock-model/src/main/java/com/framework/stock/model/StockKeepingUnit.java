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
@TableName("ybt_stock_sku")
public class StockKeepingUnit implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String spuId;
	
	// 库存属性清单 json格式{{颜色，红}，{内存，128G}}
	private String properties;

	// 显示名称，可以通过SPU名称和自己属性生成，iPhone6 红色/128G
	private String name;
	
	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private Date createTime;
	
	private Date modifyTime;
	
	@TableField(value = "is_enabled",exist = true)
	private boolean enabled;

	@TableField(value = "is_deleted",exist = true)
	private boolean deleted;
	
	private String remark;
	
	// spu信息
	@TableField(exist = false)
    private String catalogName;
}
