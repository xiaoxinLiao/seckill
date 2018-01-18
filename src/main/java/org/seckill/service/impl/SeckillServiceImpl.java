package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatkillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoxinliao
 * @date 2017/12/2 21:13
 */
//@Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Service依赖

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    /**
     * MD5 盐值字符串，用于混淆MD5
     */
    private final String salt = "as4dj3hf2ui2349@#*$%6iwh320~";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * @param seckillId
     * @return
     */
    public Exposer exportSeckillUrl(Long seckillId) {


        //Redis缓存中取出对象
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            System.out.println("数据库查询数据");
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //放入Redis
                redisDao.putSeckill(seckill);
            }
        }else{
            System.out.println("Redis中的数据");
        }


        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //当前系统时间
        Date nowTime = new Date();
        long nowTimes = nowTime.getTime();
        if (nowTimes < startTime.getTime() || nowTimes > endTime.getTime()) {

            return new Exposer(false, seckillId, nowTimes,
                    startTime.getTime(), endTime.getTime());

        }
        //根据字符串转换
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + '/' + salt;
        // DigestUtils是spring 工具类
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !getMD5(seckillId).equals(md5)) {
            throw new SeckillException("seckill data rewrite");
        }

        try {
            //执行秒杀逻辑:1记录购买行为
            // 先记录购买明细,再,可以减少行级锁的持有时间
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一验证
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatkillException("seckill Repeated");
            } else {
                //执行秒杀逻辑: 2减库存
                Date nowTime = new Date();
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束
                    throw new SeckillCloseException("seckill has been closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(
                            seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }


        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatkillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    /**
     * 使用存储过程开发秒杀逻辑
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillCloseException
     */

    public SeckillExecution executeSeckillProduce(long seckillId, long userPhone, String md5) {

        if (md5 == null || !getMD5(seckillId).equals(md5)) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行存储过程,result被赋值

        try {
            seckillDao.killProdure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao
                        .queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,
                        successKilled);
            } else{
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }



    }
}
