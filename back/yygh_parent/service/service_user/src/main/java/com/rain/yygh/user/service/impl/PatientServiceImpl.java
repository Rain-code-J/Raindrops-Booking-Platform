package com.rain.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.cmn.client.DictFeignClient;
import com.rain.yygh.common.utils.JwtHelper;
import com.rain.yygh.enums.DictEnum;
import com.rain.yygh.model.user.Patient;
import com.rain.yygh.user.mapper.PatientMapper;
import com.rain.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author rain
 * @since 2023-05-06
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> list(Wrapper<Patient> queryWrapper) {
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        patientList.forEach(item->{
            this.packagePatient(item);
        });
        return patientList;
    }

    @Override
    public List<Patient> findAllPatient(String token) {
        Long userId = JwtHelper.getUserId(token);
        List<Patient> patientList = baseMapper.selectList(new QueryWrapper<Patient>().eq("user_id", userId));
        patientList.forEach(item->{
            this.packagePatient(item);
        });
        return patientList;
    }

    @Override
    public Patient getById(Serializable id) {
        Patient patient = super.getById(id);
        this.packagePatient(patient);
        return patient;
    }

    private void packagePatient(Patient patient){
        Map<String, Object> map = patient.getParam();
        String certificatesTypeString = dictFeignClient.getNameByValueAndDictCode(DictEnum.CERTIFICATES_TYPE.getDictCode(), Long.parseLong(patient.getCertificatesType()));
        String provinceString = dictFeignClient.getRegionNameByValue(Long.parseLong(patient.getProvinceCode()));
        String cityString = dictFeignClient.getRegionNameByValue(Long.parseLong(patient.getCityCode()));
        String districtString = dictFeignClient.getRegionNameByValue(Long.parseLong(patient.getDistrictCode()));

        map.put("certificatesTypeString",certificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
    }
}
