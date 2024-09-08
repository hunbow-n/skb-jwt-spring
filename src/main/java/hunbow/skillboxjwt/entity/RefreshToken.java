package hunbow.skillboxjwt.entity;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@RedisHash("refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @Indexed
    private Long id;

    @Indexed
    private String token;

    @Indexed
    private Long userId;

    @Indexed
    private Instant expiresAt;
}
