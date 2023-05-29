package com.rain.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rain.yygh.cmn.client.DictFeignClient;
import com.rain.yygh.enums.DictEnum;
import com.rain.yygh.hosp.repository.HospitalRepository;
import com.rain.yygh.hosp.service.HospitalService;
import com.rain.yygh.model.hosp.Hospital;
import com.rain.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void saveHospital(Map<String, Object> paramMap) {
        String paramMapJSOnString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(paramMapJSOnString, Hospital.class);

        // 在MongoDB中查询是否有这个数据
        Hospital hospitalByHoscode = hospitalRepository.findHospitalByHoscode(hospital.getHoscode());
        if (hospitalByHoscode != null) {
            // 已经存在：实现修改操作
            hospital.setStatus(hospitalByHoscode.getStatus());
            hospital.setCreateTime(hospitalByHoscode.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(hospitalByHoscode.getIsDeleted());
            // 根据id更新
            hospital.setId(hospitalByHoscode.getId());
            hospitalRepository.save(hospital);
        } else {
            //  不存在：实现添加操作
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.findHospitalByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
//        if (!StringUtils.isEmpty(hospitalQueryVo.getProvinceCode()) &&
//                !StringUtils.isEmpty(hospitalQueryVo.getCityCode())) {
            BeanUtils.copyProperties(hospitalQueryVo, hospital);
//        }

        //创建匹配器，即如何使用查询条件
//        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
//                .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.startsWith())
////                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
//                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("hosname", match -> match.regex())
                .withIgnoreCase();

        Example<Hospital> example = Example.of(hospital, matcher);
        Sort sort = Sort.by("creatTime").ascending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<Hospital> pageList = hospitalRepository.findAll(example, pageable);

        pageList.getContent().stream().forEach(hospital1 -> {
            this.packageHospital(hospital1);
        });
        return pageList;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 1 || status == 0) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital detail(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        this.packageHospital(hospital);
        return hospital;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        // 模糊查询
        List<Hospital> hospitalByHosname = hospitalRepository.findHospitalByHosnameLike(hosname);
        return hospitalByHosname;
    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.findHospitalByHoscode(hoscode);
        this.packageHospital(hospital);
        return hospital;
    }

    private void packageHospital(Hospital hospital1) {
        Long hostype = Long.parseLong(hospital1.getHostype());
        Long provinceCode = Long.parseLong(hospital1.getProvinceCode());
        Long cityCode = Long.parseLong(hospital1.getCityCode());
        Long districtCode = Long.parseLong(hospital1.getDistrictCode());

        String provinceName = dictFeignClient.getRegionNameByValue(provinceCode);
        String cityName = dictFeignClient.getRegionNameByValue(cityCode);
        String districtName = dictFeignClient.getRegionNameByValue(districtCode);

        String houtypeName = dictFeignClient.getNameByValueAndDictCode(DictEnum.HOSTYPE.getDictCode(), hostype);

        Map<String, Object> map = new HashMap<>();
        map.put("level", houtypeName);
        map.put("address", provinceName + cityName + districtName + hospital1.getAddress());
        hospital1.setParam(map);
    }
}
