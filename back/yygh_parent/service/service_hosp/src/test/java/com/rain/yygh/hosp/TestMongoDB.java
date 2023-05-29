package com.rain.yygh.hosp;

import com.rain.yygh.hosp.bean.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest
public class TestMongoDB {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     *
     */
    @Test
    public void test(){
        mongoTemplate.insert(new Actor(2,"梁朝伟",true,new Date()),"actor");
    }

    /**
     * 测试新增
     * insert()方法只能做新增，不能做修改
     * 并且不能主键冲突
     * 除了使用insert还可以使用save方法
     * save既可以添加也可以修改
     * save是覆盖式的修改
     * 和MyBatisPlus的不一样，MybatisPlus是只修改你带有的
     */
    @Test
    public void testInsert(){
        List<Actor> list = new ArrayList<>();
        list.add(new Actor(1,"张馨予",false,new Date()));
        list.add(new Actor(3,"张杰",true,new Date()));
        list.add(new Actor(4,"李文婧",false,new Date()));
        list.add(new Actor(5,"董俊卓",true,new Date()));
        list.add(new Actor(6,"邓紫棋",false,new Date()));
        mongoTemplate.insert(list,Actor.class);
    }

    /**
     * 测试删除
     */
    @Test
    public void testRemove(){
        Query query = new Query();
        mongoTemplate.remove(query,Actor.class);
    }

    /**
     * 测试修改
     */
    @Test
    public void testUpdate(){
        Query query = new Query(Criteria.where("gender").is(false));
        Update update = new Update();
        update.set("name","那英");
        mongoTemplate.updateMulti(query,update,Actor.class);
    }

    /**
     * 测试查询
     */
    @Test
    public void testQuery(){
        Pattern pattern = Pattern.compile(".*张.*");

        Query query = new Query(Criteria.where("name").regex(pattern));
        List<Actor> list = mongoTemplate.find(query.skip(1).limit(1), Actor.class);
        for (Actor actor : list) {
            System.out.println("actor = " + actor);
        }
    }
}
