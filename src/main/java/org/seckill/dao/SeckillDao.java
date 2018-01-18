package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀库存DAO接口
 * @author xiaoxinliao
 * @date 2017/12/1 13:55
 */
public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId  ：减库存的id
     * @param killTime  : 秒杀时间
     * @return 如果影响行数>1,表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀商品对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);


    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);

    /**
     * 使用存储过程执行秒杀逻辑
     * @param paramMap
     */
    void killProdure(Map<String,Object> paramMap);

}
