package com.framework.stock.constant;

/**
 * @Description: 常量枚举类
 * @author: lihaoqi
 * @date: 2019年4月17日
 */
public class StockConstantEnum {

	/**
	 * @Description: 入库类型
	 * @author: lihaoqi
	 */
	public static enum WAREHOUSING_TYPE {
		PURCHASE("采购入库"), RETURNGOODS("退货入库"), ALLOT_ENTER("调仓入库"), OTHER("其他入库");

		private String value;

		private WAREHOUSING_TYPE(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		public static boolean containValue(String key) {
			WAREHOUSING_TYPE[] enums = values();
			for (WAREHOUSING_TYPE entity : enums) {
				if (entity.value.equals(key)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * @Description: 出库类型
	 * @author: lihaoqi
	 */
	public static enum OUTWAREHOUSE_TYPE {
		APPROVE("申请出库"), SALE_ORDER("销售订单出库"), SALE_ORDER_RESERVED("预留库存出库"), 
		ALLOT_OUT("调仓出库"), OTHER("其他出库");

		private String value;

		private OUTWAREHOUSE_TYPE(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		public static boolean containValue(String key) {
			OUTWAREHOUSE_TYPE[] enums = values();
			for (OUTWAREHOUSE_TYPE entity : enums) {
				if (entity.value.equals(key)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @Description: 审批状态
	 * @author: lihaoqi
	 */
	public static enum APPLY_STATUS {
		APPLY("已申请"), PASS("审批通过"), REJECT("审批拒绝");

		private String value;

		private APPLY_STATUS(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
	
	/**
	 * @Description: 库存变化类型(除了出入库变化以外的变化)
	 * @author: lihaoqi
	 */
	public static enum STOCK_CHANGE_TYPE {
		LOCKSTOCK("锁定库存"), UNLOCKSTOCK("释放库存");

		private String value;

		private STOCK_CHANGE_TYPE(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
	
	/**
	 * @Description: 盘点类型
	 * @author: lihaoqi
	 */
	public static enum CHECK_STOCK_TYPE {
		AUTO("自动盘点"), CONTROL("手动盘点");

		private String value;

		private CHECK_STOCK_TYPE(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
	
	/**
	 * @Description: 盘点状态
	 * @author: lihaoqi
	 */
	public static enum CHECK_STOCK_STATUS {
		NORMAL("正常"), UNUSUAL("异常");

		private String value;

		private CHECK_STOCK_STATUS(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
	
}
