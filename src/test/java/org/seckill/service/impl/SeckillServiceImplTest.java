package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatkillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author xiaoxinliao
 * @date 2017/12/3 10:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {


    @Autowired
    SeckillService seckillService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("list={}", seckillList);
    }

    @Test
    public void getById() throws Exception {
        long id = 1001;
        Seckill seckill = seckillService.getById(id);
        logger.info("Seckill={}", seckill);
    }

    @Test
    public void seckillLogic() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("Exposer={}", exposer);

        if(exposer.isExposed()){
            long userPhone = 13106933671L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("result={}", seckillExecution);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (RepeatkillException e) {
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }

        }else{
            logger.warn("exposer={}",exposer);
        }
    }

    @Test
    public void executeSeckillProduce() throws Exception {
        long id = 1002;
        long phone = 13113113132L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillProduce(id,phone,md5);
            logger.info("stateInfo:",seckillExecution.getStateInfo());
        }else{

            logger.info("stateInfo:","秒杀结束");
        }
    }

}