package com.rain.yygh.hosp;

import com.rain.yygh.hosp.bean.Actor;
import com.rain.yygh.hosp.repository.ActorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class TestRepository {
    @Autowired
    private ActorRepository actorRepository;

    /**
     * 测试新增
     */
    @Test
    public void testInsert(){
        List<Actor> list = new ArrayList<>();
        list.add(new Actor(1,"张馨予",false,new Date()));
        list.add(new Actor(3,"张杰",true,new Date()));
        list.add(new Actor(4,"李文婧",false,new Date()));
        list.add(new Actor(5,"董俊卓",true,new Date()));
        list.add(new Actor(6,"邓紫棋",false,new Date()));
        actorRepository.insert(list);

//        actorRepository.insert(new Actor(null,"董俊卓",true,new Date()));
    }

    /**
     * 测试删除
     */
    @Test
    public void testRemove(){
        Actor actor = new Actor();
        actor.setId(6);
        actorRepository.delete(actor);
    }

    /**
     * 测试修改
     */
    @Test
    public void testUpdate(){
        // 先查询
        Actor actor = actorRepository.findAll().get(3);
        actor.setBirth(new Date());
        actor.setGender(false);
        actor.setName("小桌");
        actorRepository.save(actor);
    }

    /**
     * 测试查询
     */
    @Test
    public void testQuery(){
//        Actor actor = new Actor(null, "李文婧", null, null);
//
//        Example<Actor> example = Example.of(actor);
//        List<Actor> all = actorRepository.findAll(example);
//        for (Actor actor1 : all) {
//            System.out.println("actor1 = " + actor1);
//        }
        //创建匹配器，即如何使用查询条件
//        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
//                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
//
//        Actor actor = new Actor();
//        actor.setName("张");
//        Example<Actor> example = Example.of(actor,matcher);
//        List<Actor> list = actorRepository.findAll(example);
//        for (Actor actor1 : list) {
//            System.out.println("actor1 = " + actor1);
//        }

        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(2,3,sort);
        Page<Actor> all = actorRepository.findAll(pageable);
        System.out.println(all.getTotalElements());

    }
}
