package com.leonxranger.ledger.Services;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;

@Service
public class BalancedServiceCache {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "balance:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);


    BalancedServiceCache(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    //update cache
    public void UpdateCache(String AccountCode, BigDecimal Currentbalance){
        String CacheKey = KEY_PREFIX + AccountCode;
        redisTemplate.opsForValue().setIfAbsent(CacheKey , Currentbalance.toString(),CACHE_TTL);
    }

    //getBalance
    public BigDecimal getBalance(String AccountCode){
        String cacheKey = KEY_PREFIX + AccountCode;

        String balance = redisTemplate.opsForValue().get(cacheKey);

        if (balance != null){
            return new BigDecimal(balance);
        }
        else return null;
    }

    //delete from cache
    public void clearCache(String AccountCode){
        if(redisTemplate.opsForValue().get(AccountCode) != null){
            redisTemplate.delete(AccountCode);
        }
    }

    //get all keys for reconciliation
    public Set<String> GetAllKeys(){
        return redisTemplate.keys(KEY_PREFIX + "*");
    }

}