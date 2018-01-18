package org.seckill.exception;

/**
 * 重复秒杀异常
 * @author xiaoxinliao
 * @date 2017/12/2 20:14
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message){
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
