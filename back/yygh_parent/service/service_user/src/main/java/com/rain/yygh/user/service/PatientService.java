package com.rain.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.user.Patient;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author rain
 * @since 2023-05-06
 */
public interface PatientService extends IService<Patient> {

    List<Patient> findAllPatient(String token);
}
