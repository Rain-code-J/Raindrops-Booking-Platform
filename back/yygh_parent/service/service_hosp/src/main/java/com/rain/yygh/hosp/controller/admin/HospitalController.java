package com.rain.yygh.hosp.controller.admin;

import com.rain.yygh.common.result.R;
import com.rain.yygh.hosp.service.HospitalService;
import com.rain.yygh.model.hosp.Hospital;
import com.rain.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/{pageNum}/{pageSize}")
    public R getHospitalPage(
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,
            HospitalQueryVo hospitalQueryVo){

        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(pageNum,pageSize,hospitalQueryVo);

        return R.ok()
                .data("total",hospitalPage.getTotalElements())
                .data("list",hospitalPage.getContent());
    }


    @ApiOperation("修改医院状态")
    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable("id") String id,@PathVariable("status") Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }

    @ApiOperation("医院详情")
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") String id){
        Hospital hospital = hospitalService.detail(id);
        return R.ok().data("hospital",hospital);
    }

}
