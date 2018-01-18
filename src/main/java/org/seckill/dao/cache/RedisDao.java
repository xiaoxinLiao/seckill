package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis 数据访问对象
 *
 * @author xiaoxinliao
 * @date 2018/1/16 22:26
 */
public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    /**
     * 获取Seckill对象
     *
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(long seckillId) {
        //redis 操作逻辑
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckillId;
            //没有实现内部序列化
            //get ->Byte[] -> 反序列化  -> Object(Seckill)
            //自定义序列化
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                //空对象
                Seckill seckill = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                // seckill 被序列化
                return seckill;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 缓存Seckill对象
     *
     * @param seckill
     */
    public String putSeckill(Seckill seckill) {
        // set Object(Seckill)->序列化  ->Byte[]
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckill.getSeckillId();
            byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

            // 超时缓存
            int timeout = 60 * 60;
            String result = jedis.setex(key.getBytes(),timeout,bytes);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
