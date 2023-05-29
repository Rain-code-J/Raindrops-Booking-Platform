package com.rain.yygh.hosp.service;

import com.rain.yygh.model.hosp.Hospital;
import com.rain.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Hospital detail(String id);

    List<Hospital> findByHosname(String hosname);

    Hospital getHospitalByHoscode(String hoscode);
}
