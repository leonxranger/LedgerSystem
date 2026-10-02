package com.leonxranger.ledger.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class Idempotencyfilter extends OncePerRequestFilter {
     private final RedisTemplate<String,String> redisTemplate;
     private static final String CachePrefix = "IDEMP:";
    private static final Duration TTL = Duration.ofHours(24);


    Idempotencyfilter(RedisTemplate<String,String> redisTemplate){
         this.redisTemplate = redisTemplate;
    }

     @Override
    protected  void doFilterInternal(HttpServletRequest request , HttpServletResponse response , FilterChain filterChain) throws ServletException , IOException {
        String method = request.getMethod();

        if(!"POST".equalsIgnoreCase(method) || !request.getRequestURI().equalsIgnoreCase("/transactions")){
            filterChain.doFilter(request, response);
            return;
        }

        String idempotencyKey =  request.getHeader("Idempotency-Key");

        String redisKey = CachePrefix + idempotencyKey;

        Boolean hasAcquired = redisTemplate.opsForValue().setIfAbsent(redisKey , "PROCESSING" ,TTL);

        if(!hasAcquired){
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "Duplicate request: this Idempotency-Key was already used");

            return;
        }

        filterChain.doFilter(request,response);


     }
}
