package org.seckill.dto;

/**
 * 封装json 结果(所有ajax请求返回类型)
 * @author xiaoxinliao
 * @date 2017/12/3 19:49
 */
public class SeckillResult<T> {
    /**
     *  请求是否成功
     */
    private boolean success;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 错误信息
     */
    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
