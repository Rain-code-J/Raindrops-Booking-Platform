package com.rain.yygh.hosp.controller.user;

import com.rain.yygh.common.result.R;
import com.rain.yygh.hosp.service.HospitalService;
import com.rain.yygh.model.hosp.Hospital;
import com.rain.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("医院列表查询接口")
    @GetMapping("/list")
    public R getHospitalList(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(1, 100000, hospitalQueryVo);
        return R.ok().data("hospitalList",hospitalPage.getContent());
    }

    @ApiOperation("医院搜索接口")
    @GetMapping("/{hosname}")
    public R findByHosname(@PathVariable String hosname){
        List<Hospital> hospitalQueryList = hospitalService.findByHosname(hosname);
        return R.ok().data("hospitalQueryList",hospitalQueryList);
    }

    @ApiOperation("查询医院详情")
    @GetMapping("/detail/{hoscode}")
    public R getHospitalByHoscode(@PathVariable String hoscode){
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        return R.ok().data("hospital",hospital);
    }

}
