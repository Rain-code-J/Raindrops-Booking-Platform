package com.rain.yygh.user.client;

import com.rain.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-user")
public interface PatientFeignClient {
    @GetMapping("/user/userinfo/patient/{patientId}")
    Patient getPatientById(@PathVariable("patientId") Long patientId);
}
