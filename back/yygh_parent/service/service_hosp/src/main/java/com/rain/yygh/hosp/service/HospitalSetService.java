package com.rain.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hoscode);

}