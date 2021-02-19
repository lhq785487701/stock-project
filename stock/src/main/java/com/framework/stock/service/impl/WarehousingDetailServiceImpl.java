package com.framework.stock.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.framework.stock.mapper.WarehousingDetailMapper;
import com.framework.stock.model.WarehousingDetail;
import com.framework.stock.service.IWarehousingDetailService;

@Service
public class WarehousingDetailServiceImpl extends ServiceImpl<WarehousingDetailMapper, WarehousingDetail>
		implements IWarehousingDetailService {

}
