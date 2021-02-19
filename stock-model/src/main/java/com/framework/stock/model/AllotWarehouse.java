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
 * @Description 仓库调拨
 * @author lihaoqi
 * @date 2020年12月6日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_allot_warehouse")
public class AllotWarehouse implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private String allocNo;
	
	private String outWarehouseId;
	
	private String enterWarehouseId;
	
	private String applyUserId;
	
	private String approveUserId;
	
	private Date applyTime;

	private Date finishedTime;
	
	private String status;
	
	private String reason;
	
	private Date createTime;
	
	private String createUser;
	
	private Date modifyTime;
	
	private String modifyUser;
	
	@TableField(value = "is_enabled",exist = true)
	private boolean enabled;

	@TableField(value = "is_deleted",exist = true)
	private boolean deleted;
	
	private String remark;
	
	@TableField(exist = false)
    private List<AllotWarehouseDetail> allotWarehouseDetailList; // 调拨明细list
	
}
