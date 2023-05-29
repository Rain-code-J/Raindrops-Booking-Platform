package com.rain.yygh.hosp.service;

import com.rain.yygh.model.hosp.Department;
import com.rain.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    /**
     * 上传科室信息
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    Page<Department> findPage(Integer page, Integer limit, Department department);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findAllByHosCode(String hoscode);

    String getDepName(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);
}
