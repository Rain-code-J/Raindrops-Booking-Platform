package com.rain.yygh.hosp.repository;

import com.rain.yygh.hosp.bean.Actor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActorRepository extends MongoRepository<Actor,String> {

}
