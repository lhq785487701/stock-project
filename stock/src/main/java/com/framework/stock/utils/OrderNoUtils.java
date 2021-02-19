package com.framework.stock.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.framework.stock.constant.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 订单号生成工具类
 * @author lihaoqi
 * @date 2020年12月3日
 *
 */
@Slf4j
public class OrderNoUtils {

	private static long tmpID = 0;
	private static final long INCREASE_STEP = 1;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");

	/**
	 * @Description 根据时间戳生成唯一id
	 * @return
	 */
	public static synchronized long nextPkId() {
		// 当前：（年、月、日、时、分、秒、毫秒）
		long timeCount;
		timeCount = Long.parseLong(sdf.format(new Date()));
		if (tmpID < timeCount) {
			tmpID = timeCount;
		} else {
			tmpID += INCREASE_STEP;
			timeCount = tmpID;
		}
		return timeCount;
	}

	/**
	 * @Description 生成入库单号
	 * @return
	 */
	public static String createWarehousingNo() {
		String orderNo = Constants.WAREHOUSINGNO_PREFIX + nextPkId();
		log.debug("生成入库单号：{}", orderNo);
		return orderNo;
	}
	
	/**
	 * @Description 生成出库单号
	 * @return
	 */
	public static String createOutwarehouseNo() {
		String orderNo = Constants.OUTWAREHOUSENO_PREFIX + nextPkId();
		log.debug("生成出库单号：{}", orderNo);
		return orderNo;
	}
	
	/**
	 * @Description 生成调仓单号
	 * @return
	 */
	public static String createAllotWarehouseNo() {
		String orderNo = Constants.ALLOT_WAREHOUSENO_PREFIX + nextPkId();
		log.debug("生成调仓单号：{}", orderNo);
		return orderNo;
	}
	
	/**
	 * @Description 生成盘点单号
	 * @return
	 */
	public static String createCheckWarehouseNo() {
		String orderNo = Constants.CHECK_WAREHOUSENO_PREFIX + nextPkId();
		log.debug("生成盘点单号：{}", orderNo);
		return orderNo;
	}
}
