package com.rain.yygh.user.controller.user;

import com.rain.yygh.common.result.R;
import com.rain.yygh.common.utils.JwtHelper;
import com.rain.yygh.model.user.Patient;
import com.rain.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author rain
 * @since 2023-05-06
 */
@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // 增
    @PostMapping("/save")
    public R savePatient(@RequestHeader String token, @RequestBody Patient patient){
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }
    // 删
    @DeleteMapping("/remove/{id}")
    public R removePatient(@PathVariable Long id){
        patientService.removeById(id);
        return R.ok();
    }
    // 改
    // 1.去修改页面
    @GetMapping("/get/{id}")
    public R getPatient(@PathVariable Long id){
        Patient patient = patientService.getById(id);
        return R.ok().data("patient",patient);
    }
    // 2.真实修改
    @PutMapping("/update")
    public R updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return R.ok();
    }
    // 查
    @GetMapping("/findAll")
    public R findAllPatient(@RequestHeader String token){
        List<Patient> patientList  = patientService.findAllPatient(token);
        return R.ok().data("list",patientList);
    }

    @GetMapping("/{patientId}")
    public Patient getPatientById(@PathVariable("patientId") Long patientId){
        Patient patient = patientService.getById(patientId);
        return patient;
    }
}

