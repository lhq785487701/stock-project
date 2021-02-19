package com.framework.stock.exception;

import com.framework.stock.common.exception.AbstractBaseExceptionEnum;

/**
 * @Description 业务异常的枚举
 * @author lihaoqi
 * @date 2020年12月3日 
 *
 */
public enum StockServiceExceptionEnum implements AbstractBaseExceptionEnum  {

    CODE_400(400, "无法获取到资源！"),
    CODE_40000(40000, "功能未上线！"),

    CODE_500(500, "服务器异常！"),

    /** 库存错误码 */
    CODE_50001(50001, "skuid不能为空"),
    CODE_50002(50002, "库存id不能为空"),
    CODE_50037(50037, "该商品尚无库存"),
    CODE_50038(50038, "业务订单号不能为空"),
    CODE_50039(50039, "解锁库存时，订单锁定库存不足"),
    CODE_50040(50040, "预备库存不足，无法释放库存"),
    CODE_50041(50041, "同一业务订单已锁定库存"),
    CODE_50042(50042, "业务订单无锁定库存"),
    CODE_50043(50043, "业务订单已解锁库存"),
    
    /** 供应商错误码 */
    CODE_50003(50003, "供应商id不能为空"),
    CODE_50004(50004, "供应商名称不能为空"),
    CODE_50005(50005, "供应商编码不能为空"),
    CODE_50006(50006, "供应商编码重复"),
    
    /** 仓库错误码 */
    CODE_50007(50007, "仓库id不能为空"),
    CODE_50008(50008, "仓库编码不能为空"),
    CODE_50009(50009, "仓库编码重复"),
    CODE_50010(50010, "仓库名称不能为空"),
    CODE_50011(50011, "地址国家信息不能为空"),
    CODE_50012(50012, "地址省信息不能为空"),
    CODE_50013(50013, "地址市信息不能为空"),
    CODE_50014(50014, "地址区信息不能为空"),
    CODE_50015(50015, "地址详情信息不能为空"),
    CODE_50016(50016, "仓库电话不能为空"),
    CODE_50017(50017, "创建用户不能为空"),
    CODE_50033(50033, "仓库不存在"),
    
    /** 入库错误码 */
    CODE_50018(50018, "入库仓库不能为空"),
    CODE_50020(50020, "申请入库人不能为空"),
    CODE_50021(50021, "入库类型不能为空"),
    CODE_50022(50022, "入库类型不存在"),
    CODE_50023(50023, "没有入库商品"),
    CODE_50024(50024, "商品skuId不能为空"),
    CODE_50025(50025, "商品不存在"),
    CODE_50026(50026, "入库id不能为空"),
    CODE_50027(50027, "不存在该入库单"),
    CODE_50028(50028, "该入库单已经审批"),
    CODE_50029(50029, "入库审批人不能为空"),
    CODE_50030(50030, "审批状态异常"),
    CODE_50031(50031, "审批拒绝理由不能为空"),
    CODE_50032(50032, "商品入库失败，入库失败商品skuId:"),
    CODE_50034(50034, "入库单号不能为空"),
    CODE_50035(50035, "商品数量不能为空"),
    CODE_50036(50036, "存在库存入库中，请稍后再试"),
    
    /** 出库错误码 */
    CODE_50118(50118, "出库仓库不能为空"),
    CODE_50120(50120, "申请出库人不能为空"),
    CODE_50121(50121, "出库类型不能为空"),
    CODE_50122(50122, "出库类型不存在"),
    CODE_50123(50123, "没有出库商品"),
    CODE_50126(50126, "出库id不能为空"),
    CODE_50127(50127, "不存在该出库单"),
    CODE_50128(50128, "该出库单已经审批"),
    CODE_50129(50129, "出库审批人不能为空"),
    CODE_50132(50132, "商品出库失败，出库失败商品skuId:"),
    CODE_50134(50134, "出库单号不能为空"),
    CODE_50135(50135, "可用库存不足"),
    CODE_50136(50136, "销售订单出库时，业务订单号不能为空"),
    CODE_50137(50137, "释放的库存超出该商品总锁定库存"),
    CODE_50138(50138, "存在库存出库中，请稍后再试"),
    
    /** 调仓错误码 */
    CODE_50223(50223, "没有调仓商品"),
    CODE_50224(50224, "调仓单号不能为空"),
    CODE_50227(50227, "不存在该调仓单"),
    CODE_50228(50228, "该调仓单已经审批"),
    CODE_50229(50229, "调仓审批人不能为空"),
    CODE_50230(50230, "调仓仓库不能设置为一样"),
    
    /** 盘点错误码 */
    CODE_50324(50324, "盘点单号不能为空"),
    ;
	
	StockServiceExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
