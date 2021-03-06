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
 * @Description 盘点仓库详情
 * @author lihaoqi
 * @date 2020年12月6日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_check_warehouse_detail")
public class CheckWarehouseDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private String checkNo;
	
	private String skuId;
	
	private Integer enterQuantity;
	
	private Integer outQuantity;
	
	private Integer totalQuantity;
	
	private String status;
	
	private String message;

}
