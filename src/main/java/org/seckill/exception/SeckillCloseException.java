package org.seckill.exception;

/**
 * 秒杀关闭异常
 * @author xiaoxinliao
 * @date 2017/12/2 20:14
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message){
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
