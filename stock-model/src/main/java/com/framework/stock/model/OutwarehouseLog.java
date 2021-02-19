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
 * @Description 入库凭证表
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_outwarehouse_log")
public class OutwarehouseLog implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	
	private String warehouseId;
	
	private String skuId;
	
	private String skuName;
	
	private Integer quantity;
	
	private String type;

	private Date createTime;
	
	private String serialNo;
	
}
