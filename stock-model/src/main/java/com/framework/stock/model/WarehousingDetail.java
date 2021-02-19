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
 * @Description 入库明细
 * @author lihaoqi
 * @date 2020年12月3日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_warehousing_detail")
public class WarehousingDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private String warehousingNo;
	
	private String skuId;
	
	private String supplierId;
	
	private Integer quantity;
	
	private Integer sellingPrice;
	
	private Integer costPrice;
	
	private Integer sort;
	
	private String remark;

}
