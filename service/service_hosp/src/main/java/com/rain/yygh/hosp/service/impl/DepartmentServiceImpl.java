package com.rain.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rain.yygh.hosp.repository.DepartmentRepository;
import com.rain.yygh.hosp.service.DepartmentService;
import com.rain.yygh.model.hosp.Department;
import com.rain.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//实现方法
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap 转换department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);

        //根据医院编号 和 科室编号查询
        Department departmentExist = departmentRepository.
                findDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        //判断
        if (departmentExist != null) {
            department.setCreateTime(departmentExist.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            department.setId(departmentExist.getId());
            departmentRepository.save(department);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPage(Integer page, Integer limit, Department department) {
        Example example = Example.of(department);
        Pageable pageable = PageRequest.of(page - 1, limit);


        Page pageList = departmentRepository.findAll(example, pageable);
        return pageList;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findAllByHosCode(String hoscode) {
        // 查询出所有的科室信息
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> departmentList = departmentRepository.findAll(example);
        // 大科室列表
        // key:bigCode
        // value:大科室下的列表
        Map<String, List<Department>> bigDepartmentList = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        // 返回的最终列表
        List<DepartmentVo> bigDepartmentVOList = new ArrayList<>();
        // 将大科室列表进行for循环
        for (Map.Entry<String, List<Department>> entry : bigDepartmentList.entrySet()) {
            DepartmentVo bigDepartmentVo = new DepartmentVo();
            // 获取大科室编号，以及其子科室列表
            String bigCode = entry.getKey();
            List<Department> smallDepartmentList = entry.getValue();

            // 设置返回的大科室VO的名字和编码
            bigDepartmentVo.setDepcode(bigCode);
            bigDepartmentVo.setDepname(smallDepartmentList.get(0).getBigname());
            // 设置大科室的子科室列表
            List<DepartmentVo> children = new ArrayList<>();
            for (Department smallDepartment : smallDepartmentList) {
                DepartmentVo smallDepartmentVo = new DepartmentVo();
                smallDepartmentVo.setDepcode(smallDepartment.getDepcode());
                smallDepartmentVo.setDepname(smallDepartment.getDepname());
                // 将每一个子科室都添加到子科室列表中去
                children.add(smallDepartmentVo);
            }
            // 设置每一个大科室的子科室列表
            bigDepartmentVo.setChildren(children);
            // 将每一个大科室都添加到大科室列表中去
            bigDepartmentVOList.add(bigDepartmentVo);
        }
        // 将大科室列表返回
        return bigDepartmentVOList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
        return department.getDepname();
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode,depcode);
    }
}