package hunbow.skillboxjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "hunbow.skillboxjwt.repository")
public class SkillboxJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillboxJwtApplication.class, args);
    }

}