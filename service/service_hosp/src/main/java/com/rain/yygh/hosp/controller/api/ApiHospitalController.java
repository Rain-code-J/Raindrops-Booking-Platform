package com.rain.yygh.hosp.controller.api;

import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.hosp.bean.Result;
import com.rain.yygh.hosp.service.HospitalService;
import com.rain.yygh.hosp.service.HospitalSetService;
import com.rain.yygh.hosp.utils.HttpRequestHelper;
import com.rain.yygh.hosp.utils.MD5;
import com.rain.yygh.model.hosp.Hospital;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;


    @ApiOperation("上传医院")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        // 获取请求参数map
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 将Map<String, String[]>  -->  Map<String,Object>
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)) {
            throw new YyghException(20001, "医院编码错误");
        }

        String signRequest = (String) paramMap.get("sign");
        if (StringUtils.isEmpty(signRequest)) {
            throw new YyghException(20001, "签名数据为空");
        }

        // 根据hoscode查询医院设置信息 --> 得到signKey
        String signKey = hospitalSetService.getSignKey(hoscode);
        String encrypt = MD5.encrypt(signKey);

        if (!signRequest.equals(encrypt)) {
            throw new YyghException(20001,"签名数据错误");
        }

        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData = (String)paramMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        paramMap.put("logoData",logoData);

        hospitalService.saveHospital(paramMap);

        return Result.ok();
    }

    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Result<Hospital> hospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(20001,"失败");
        }
        //签名校验
//        String signRequest = (String) paramMap.get("sign");
//        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("hoscode",hoscode);
//        HospitalSet hospitalSet = hospitalSetService.getOne(queryWrapper);
//        String signKey = hospitalSet.getSignKey();
//        String encrypt = MD5.encrypt(signKey);
//        if (signRequest.equals(encrypt)){
            Hospital hospital = hospitalService.getByHoscode(hoscode);
            return Result.ok(hospital);
//        } else {
//            throw new YyghException(20001,"签名数据错误");
//        }
    }
}
