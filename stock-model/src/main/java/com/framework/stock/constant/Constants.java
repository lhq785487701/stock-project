package com.framework.stock.constant;

/**
 * @Description 库存相关常量类
 * @author lihaoqi
 * @date 2020年12月1日 
 *
 */
public interface Constants {

	// 存储可用库存在redis中的前缀
	public static final String STOCK_AVAILABLE_QUANTITY_REDIS_PREFIX = "com:framework:stock:quantity:";
	// 商品出入库分布式锁redis前缀
	public static final String STOCK_REDISNX_LOCK = "com:framework:stock:lock:";
	// 获锁等待时间（ms）
	public static final Integer STOCK_REDISNX_TIMEOUT = 10000;
	
	// 入库单生成前缀
	public static final String WAREHOUSINGNO_PREFIX = "WI";
	// 出库单生成前缀
	public static final String OUTWAREHOUSENO_PREFIX = "WO";
	// 调仓单生成前缀
	public static final String ALLOT_WAREHOUSENO_PREFIX = "WA";
	// 盘点单生成前缀
	public static final String CHECK_WAREHOUSENO_PREFIX = "WC";
	// 自动审批时审批人
	public static final String AUTO_APPROVE_USER = "system";
	
	
	
	/** 成功返回 */
	public static final String APPLY_WAREHOUSING_SUCCESS = "入库申请成功";
	public static final String WAREHOUSING_SUCCESS = "审批通过，入库成功";
	public static final String WAREHOUSING_REJECT = "入库审批不通过";
	public static final String APPLY_OUTWAREHOUSE_SUCCESS = "出库申请成功";
	public static final String OUTWAREHOUSE_SUCCESS = "审批通过，出库成功";
	public static final String OUTWAREHOUSE_REJECT = "出库审批不通过";
	public static final String APPLY_ALLOTWAREHOUSE_SUCCESS = "调仓申请成功";
	public static final String ALLOTWAREHOUSE_SUCCESS = "审批通过，调仓成功";
	public static final String ALLOTWAREHOUSE_REJECT = "调仓审批不通过";
	public static final String LOCKSTOCK_SUCCESS = "锁定库存成功";
	public static final String UNLOCKSTOCK_SUCCESS = "解锁库存成功";
	public static final String STOCK_QUANTITY_ERROR = "库存总数量与出入库数量不相等";
	public static final String STOCK_SKU_ERROR = "库存中存在商品不存在";
	
	
	
}
