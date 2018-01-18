package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author xiaoxinliao
 * @date 2017/12/2 13:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {

    /**
     * @RunWith(SpringJUnit4ClassRunner.class)
     * 1.配置spring和junit整合，junit启动时加载spring IOC容器
     *  spring-test,junit
     *
     *  @ContextConfiguration({"classpath:spring/spring-dao.xml"})
     * 2.告诉junit spring 配置文件的位置
     */

    //注入依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception {
        long id = 1001;
        Seckill seckill =  seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {

        List<Seckill> seckillList = seckillDao.queryAll(0,100);
        for (Seckill seckill:seckillList){
            System.out.println(seckill);
        }
    }

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1001L,killTime);
        System.out.println("updateCount=" + updateCount);
    }



}