package com.rain.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.hosp.mapper.HospitalSetMapper;
import com.rain.yygh.hosp.service.HospitalSetService;
import com.rain.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {


    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(queryWrapper);

        if(hospitalSet == null) {
            throw new YyghException(20001,"获取医院设置错误");
        }

        String signKey = hospitalSet.getSignKey();

        return signKey;
    }
}