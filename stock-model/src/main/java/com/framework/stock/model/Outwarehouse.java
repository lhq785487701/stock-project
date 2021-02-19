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
 * @Description 入库表
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("ybt_outwarehouse")
public class Outwarehouse implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private String outwarehouseNo;
	
	private String warehouseId;
	
	private String applyUserId;
	
	private String approveUserId;
	
	private String type;
	
	private String orderNo;
	
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
    private List<OutwarehouseDetail> outwarehouseDetailList; // 出库明细list
	
}
