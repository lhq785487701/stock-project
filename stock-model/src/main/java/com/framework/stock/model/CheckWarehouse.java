package com.framework.stock.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 盘点仓库
 * @author lihaoqi
 * @date 2020年12月6日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_check_warehouse")
public class CheckWarehouse implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private String checkNo;
	
	private String warehouseId;
	
	private Integer totalQuantity;

	private Integer totalCount;
	
	private Date startTime;

	private Date finishedTime;
	
	private String status;
	
	private String type;
	
	private Date createTime;
	
	private String createUser;
	
	private Date modifyTime;
	
	private String modifyUser;
	
	@TableField(value = "is_deleted", exist = true)
	private boolean deleted;
	
	private String remark;
	
	@TableField(exist = false)
    private List<CheckWarehouseDetail> checkWarehouseDetailList; // 盘点明细list

}
