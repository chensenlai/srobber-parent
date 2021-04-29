package com.srobber.auth.support;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.srobber.auth.TokenCacheManager;
import com.srobber.auth.UserLoginInfo;
import com.srobber.common.spring.EnvironmentContext;
import com.srobber.common.util.DateUtil;
import com.srobber.common.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 * 登录token缓存管理器
 *
 * @author chensenlai
 */
@Slf4j
public class LocalTokenCacheManager implements TokenCacheManager, InitializingBean {

    private DelayQueue<DelayedToken> deleteTokenQueue = new DelayQueue<>();
    private int timeoutMillis = 30*1000;

    private Cache<String, UserLoginInfo> localTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @Override
    public String putLoginInfo(UserLoginInfo loginInfo) {
        String token = StringUtil.uuid();
        localTokenCache.put(token, loginInfo);
        return token;
    }

    @Override
    public UserLoginInfo getLoginInfo(String token) {
        return localTokenCache.getIfPresent(token);
    }

    @Override
    public void deleteLoginInfo(String token) {
        //延迟队列删除token, 避免客户端网络并发请求,
        //重刷token后,旧token立刻失效导致部分请求失败
        deleteTokenQueue.put(new DelayedToken(DateUtil.nowMillis()+timeoutMillis, token));
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        this.startCleanExpireToken();

        if(!EnvironmentContext.isProdEnv()) {
            UserLoginInfo loginInfo = new UserLoginInfo();
            loginInfo.setUserId(10000001);
            localTokenCache.put("123456", loginInfo);
        }
    }

    private void startCleanExpireToken() {
        Thread tokenCleanThread = new Thread(()->{
            while(true) {
                try {
                    DelayedToken delayedToken = deleteTokenQueue.take();
                    if(delayedToken != null) {
                        String token = delayedToken.getToken();
                        localTokenCache.invalidate(token);
                    }
                } catch (Exception e) {
                    log.warn("token clean error", e);
                }
            }
        });
        tokenCleanThread.setName("token-cleaner");
        tokenCleanThread.start();
    }

    @Getter
    static class DelayedToken implements Delayed {

        private long expireTime;
        private String token;

        public DelayedToken(long expireTime, String token) {
            this.expireTime = expireTime;
            this.token = token;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expireTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if(this == o) {
              return 0;
            }
            if (o instanceof DelayedToken) {
                DelayedToken dt = (DelayedToken) o;
                long diff = this.expireTime - dt.expireTime;
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                }
            }
            long d = (getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }
    }
}
