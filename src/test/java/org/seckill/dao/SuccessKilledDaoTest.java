package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author xiaoxinliao
 * @date 2017/12/2 16:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        Long userPhone= 13710399496L;
        int count = successKilledDao.insertSuccessKilled(1000L,userPhone);
        System.out.println("count=" + count);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        Long userPhone= 13710399496L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000L,userPhone);

        System.out.println(successKilled.getSeckill());
        System.out.println(successKilled);
    }

}