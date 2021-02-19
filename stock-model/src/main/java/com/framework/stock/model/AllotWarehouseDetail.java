package com.framework.stock.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 调拨商品明细
 * @author lihaoqi
 * @date 2020年12月6日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_allot_warehouse_detail")
public class AllotWarehouseDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private String allocNo;
	
	private String skuId;
	
	private Integer quantity;
	
	private Integer sort;
	
	private String remark;

}
