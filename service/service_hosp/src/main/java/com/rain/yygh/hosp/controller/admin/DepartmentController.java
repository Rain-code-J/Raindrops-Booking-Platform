package com.rain.yygh.hosp.controller.admin;

import com.rain.yygh.common.result.R;
import com.rain.yygh.hosp.service.DepartmentService;
import com.rain.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hoscode}")
    public R getDepartmentList(@PathVariable("hoscode") String hoscode){
        // 查询出所有的科室信息
        List<DepartmentVo> departmentList = departmentService.findAllByHosCode(hoscode);
        return R.ok().data("departmentList",departmentList);
    }

}
