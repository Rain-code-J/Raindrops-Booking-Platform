package com.rain.yygh.hosp.repository;

import com.rain.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HospitalRepository extends MongoRepository<Hospital,String> {

    Hospital findHospitalByHoscode(String houcode);

    List<Hospital> findHospitalByHosnameLike(String hosname);

}
