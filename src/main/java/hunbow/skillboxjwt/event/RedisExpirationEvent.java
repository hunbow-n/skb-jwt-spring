package hunbow.skillboxjwt.event;


import hunbow.skillboxjwt.entity.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisExpirationEvent {

    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<RefreshToken> event) {
        RefreshToken refreshToken = (RefreshToken) event.getValue();

        if(refreshToken == null) {
            throw new RuntimeException("Refresh token is null in handleRedisKeyExpiredEvent function");
        }

        log.info("Refresh token with key {} is expired, refresh token is {}", refreshToken.getId(),
                refreshToken.getToken());

    }

}
