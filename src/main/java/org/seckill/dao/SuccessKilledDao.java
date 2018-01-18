package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * 秒杀成功明细DAO接口
 * @author xiaoxinliao
 * @date 2017/12/1 14:08
 */
public interface SuccessKilledDao {

    /**
     * 添加秒杀成功明细
     * @param seckillId: 秒杀商品的id
     * @param userPhone: 用户手机号码
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀商品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone") long userPhone);
}
