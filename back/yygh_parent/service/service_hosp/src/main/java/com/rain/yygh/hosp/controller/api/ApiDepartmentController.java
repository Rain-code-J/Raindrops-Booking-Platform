package com.rain.yygh.hosp.controller.api;

import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.hosp.bean.Result;
import com.rain.yygh.hosp.service.DepartmentService;
import com.rain.yygh.hosp.utils.HttpRequestHelper;
import com.rain.yygh.model.hosp.Department;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = "医院科室管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "上传科室")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略
        //签名校验

//        String s = JSONObject.toJSONString(Result.ok());
//
        departmentService.save(paramMap);
//        response.setContentType("application/json; charset=utf-8");
//        response.getWriter().write(s);
        return Result.ok();
    }

    @ApiOperation("分页查询科室信息")
    @PostMapping("/department/list")
    public Result department(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数验证
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(20001,"医院编号异常");
        }

        Integer page = Integer.parseInt((String)paramMap.get("page"));
        Integer limit = Integer.parseInt((String)paramMap.get("limit"));

        Department department = new Department();
        department.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.findPage(page,limit,department);

        return Result.ok(pageModel);

    }

    @ApiOperation(value = "删除科室信息")
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        Long timestamp = Long.parseLong((String) paramMap.get("timestamp"));
        String sign = (String) paramMap.get("sign");

        departmentService.remove(hoscode,depcode);

        return Result.ok();
    }

}
