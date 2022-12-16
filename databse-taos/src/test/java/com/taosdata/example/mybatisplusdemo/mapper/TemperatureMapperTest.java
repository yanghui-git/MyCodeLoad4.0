package com.taosdata.example.mybatisplusdemo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taosdata.example.mybatisplusdemo.domain.Temperature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TemperatureMapperTest {

    private static Random random = new Random(System.currentTimeMillis());
    private static String[] locations = {"北京", "上海", "深圳", "广州", "杭州"};

    //@Before
    @Test
    public void before() {
        mapper.dropSuperTable();
        // create table temperature  创建超级表 注意语句tag是标记子表
        mapper.createSuperTable();
        // create table t_X using temperature  // 创建子表 注意tag 以及各自的表名称tb+i
        for (int i = 0; i < 10; i++) {
            mapper.createTable("tb" + i, locations[random.nextInt(locations.length)], i);
        }
        // insert into table //插入数据
        int affectRows = 0;
        long ts = System.currentTimeMillis();
        // insert 10 tables
        for (int i = 0; i < 10; i++) {
            // each table insert 5 rows
            for (int j = 0; j < 5; j++) {
                Temperature one = new Temperature();
                // 如果是插入单条记录，这样写没问题，可这里是在循环体里，TDengine表中时间戳为主键，相同的时间戳会发生覆盖，导致最终插入的记录数量与预期不符
//                one.setTs(new Timestamp(1605024000000l));
                Timestamp timestamp = new Timestamp(ts + j);
                one.setTs(timestamp);
                one.setTemperature(random.nextFloat() * 50);
                one.setLocation("望京");
                one.setTbIndex(i);
                affectRows += mapper.insertOne(one);
            }
        }
        Assert.assertEquals(50, affectRows);
    }

    //@After
    @Test
    public void after() {
        mapper.dropSuperTable();
    }

    @Autowired
    private TemperatureMapper mapper;

    /***
     * test SelectList
     * **/
    @Test
    public void testSelectList() {
        List<Temperature> temperatureList = mapper.selectList(null);
       // temperatureList.forEach(System.out::println);
    }

    /***
     * test InsertOne which is a custom metheod
     * ***/
    @Test
    public void testInsert() {
        Temperature one = new Temperature();
        one.setTs(new Timestamp(1605024000000l));
        one.setTemperature(random.nextFloat() * 50);
        one.setLocation("望京");
        int affectRows = mapper.insertOne(one);
        Assert.assertEquals(1, affectRows);
    }

    /***
     * test SelectOne
     * **/
    @Test
    public void testSelectOne() {
        QueryWrapper<Temperature> wrapper = new QueryWrapper<Temperature>();
        wrapper.eq("location", "望京");
        Temperature one = mapper.selectOne(wrapper);
        System.out.println(one);
        Assert.assertNotNull(one);
    }

    /***
     * test  select By map
     * ***/
    @Test
    public void testSelectByMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("location", "北京");
        List<Temperature> temperatures = mapper.selectByMap(map);
        System.out.println(temperatures);
    }

    /***
     * test selectObjs
     * **/
    @Test
    public void testSelectObjs() {
        // 只返回第一个字段的值
        List<Object> ts = mapper.selectObjs(null);
        System.out.println(ts);
    }

    /**
     * test selectC ount
     * **/
    @Test
    public void testSelectCount() {
        int count = mapper.selectCount(null);
        Assert.assertEquals(50, count);
    }

    /****
     * 分页
     */
    @Test
    public void testSelectPage() {
        IPage page = new Page(1, 2);
        IPage<Temperature> temperatureIPage = mapper.selectPage(page, null);
        System.out.println("total : " + temperatureIPage.getTotal());
        System.out.println("pages : " + temperatureIPage.getPages());
        for (Temperature temperature : temperatureIPage.getRecords()) {
            System.out.println(temperature);
        }
    }

}