package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//提供了限流的基本服务
@Service
public class RedisLimitManager {
    @Resource
    private RedissonClient redissonClient;
    /**
     * 限流
     * @param key  区分不同的限流器，不同用户的id
     * */
    public void doRateLimit(String key){
        //创建一个限流器(key名称)，每秒最多访问2次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);
        //每个用户可以每秒请求调用几个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if(!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

    }
}
