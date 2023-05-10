package com.rain.yygh.hosp.repository;

import com.rain.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department findDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}