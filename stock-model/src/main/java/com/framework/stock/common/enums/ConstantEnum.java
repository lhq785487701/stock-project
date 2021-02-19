package com.framework.stock.common.enums;

/**
 * @Description: 常量枚举类
 * @author: lihaoqi
 * @date: 2019年4月17日
 */
public class ConstantEnum {

	/**
	 * @Description: 是否可用 NO-0-禁用 YES-1-启用
	 * @author: lihaoqi
	 */
	public static enum IS_ENABLED {
		NO(false), YES(true);

		private boolean value;

		private IS_ENABLED(boolean value) {
			this.value = value;
		}

		public boolean value() {
			return this.value;
		}
	}

	/**
	 * @Description: 是否删除 NO-0-未删除 YES-1-已删除
	 * @author: lihaoqi
	 */
	public static enum IS_DELETE {
		NO(false), YES(true);

		private boolean value;

		private IS_DELETE(boolean value) {
			this.value = value;
		}

		public boolean value() {
			return this.value;
		}
	}

	/**
	 * @Description: 是否使用 NO-0-未使用 YES-1-已使用 LOSE-2-过期
	 * @author: lihqoqi
	 */
	public static enum IS_USED {
		NO(0), YES(1), LOSE(2);

		private int value;

		private IS_USED(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	/**
	 * @Description: 是否默认
	 * @author: lihaoqi
	 * @date: 2019年7月10日 下午11:10:38
	 */
	public static enum IS_DEFAULT {
		NO(0), YES(1);

		private int value;

		private IS_DEFAULT(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}
	
}
