package com.rain.yygh.hosp.controller.user;

import com.rain.yygh.common.result.R;
import com.rain.yygh.hosp.service.DepartmentService;
import com.rain.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "科室显示接口")
@RestController
@RequestMapping("/user/hosp/department")
public class UserDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/all/{hoscode}")
    public R findAll(@PathVariable String hoscode){
        List<DepartmentVo> departmentVoList = departmentService.findAllByHosCode(hoscode);
        return R.ok().data("departmentVoList",departmentVoList);
    }

}
