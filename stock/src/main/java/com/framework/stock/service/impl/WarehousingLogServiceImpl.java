package com.framework.stock.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.WarehousingLogMapper;
import com.framework.stock.model.WarehousingLog;
import com.framework.stock.service.IWarehousingLogService;

@Service
public class WarehousingLogServiceImpl extends ServiceImpl<WarehousingLogMapper, WarehousingLog>
		implements IWarehousingLogService {

}
