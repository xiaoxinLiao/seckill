package org.seckill.entity;

import java.util.Date;

/**
 * 秒杀成功明细
 * @author xiaoxinliao
 * @date 2017/12/1 13:45
 */
public class SuccessKilled {

    /**
     * 秒杀商品id
     */
    private long seckillId;

    /**
     *用户手机号
     */
    private long userPhone;

    /**
     * 状态：-1：无效 0：成功 1：已付款 2：已发货
     */
    private short state;

    /**
     *  创建时间
     */
    private Date createTime;

    /**
     * 多对一  秒杀库存实体类的一个实例
     */
    private Seckill seckill;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
