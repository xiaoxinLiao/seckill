-- 秒杀执行存储过程
DELIMITER $$ -- ; 转化为 $$


-- 定义存储过程
-- 参数 in 输入参数 out 输出参数
-- row_count(): 返回上一条修改类型(delete,insert,update)的影响行数
-- row_count(): 0:未修改的行数; >0: 表示修改的行数; <0:SQL错误/未执行修改sql
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id bigint,in v_phone bigint,
  in v_kill_time TIMESTAMP ,out r_result int)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    INSERT ignore into success_killed
    (seckill_id, user_phone,create_time)
      VALUES (v_seckill_id,v_phone,v_kill_time);
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0) THEN
      ROLLBACK;
      set r_result = -1;
    ELSEIF (insert_count < 0) THEN
      ROLLBACK;
      set r_result = -2;
    ELSE
      UPDATE seckill
        SET number = number - 1
      WHERE seckill_id = v_seckill_id
      AND start_time < v_kill_time
      AND  end_time > v_kill_time
      AND number > 0;
      SELECT row_count() INTO insert_count;
      IF (insert_count = 0) THEN
        ROLLBACK;
        set r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;
      ELSE
        COMMIT;
        SET r_result = 1;
      END IF;
    END IF;
  END;
$$
-- 存储过程结束
-- 修改换行符
DELIMITER ;

-- 定义变量
SET @r_result = -3;

-- 调用存储过程
call execute_seckill(1002,13106933600,now(),@r_result);
SELECT @r_result;