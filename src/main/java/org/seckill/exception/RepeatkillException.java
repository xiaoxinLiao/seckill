package org.seckill.exception;

/**
 * 重复秒杀异常
 * @author xiaoxinliao
 * @date 2017/12/2 20:14
 */
public class RepeatkillException extends SeckillException {

    public RepeatkillException(String message){
        super(message);
    }

    public RepeatkillException(String message, Throwable cause) {
        super(message, cause);
    }
}
