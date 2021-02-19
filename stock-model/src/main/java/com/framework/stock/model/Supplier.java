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
@TableName("ybt_stock_supplier")
public class Supplier implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String name;
	
	private String postalCode;
	
	private String address;
	
	private String telephone;
	
	private String fax;
	
	private String contactName;
	
	private String contactPhone;
	
	private String contactEmail;
	
	@TableId(value = "id",type = IdType.ID_WORKER_STR)
	private String id;
	
	private Date createTime;
	
	private String createUser;
	
	private Date modifyTime;
	
	private String modifyUser;
	
	@TableField(value = "is_enabled",exist = true)
	private boolean enabled;

	@TableField(value = "is_deleted",exist = true)
	private boolean deleted;
	
	private String remark;
	
}

