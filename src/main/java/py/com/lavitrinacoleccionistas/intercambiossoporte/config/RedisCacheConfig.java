
package py.com.lavitrinacoleccionistas.intercambiossoporte.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${app.cache.intercambio-ttl}") long intercambioTtl,
            @Value("${app.cache.ticket-ttl}") long ticketTtl) {

        var serializer = new GenericJacksonJsonRedisSerializer(
                new ObjectMapper()
        );

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer))
                        .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> caches = Map.of(
                "intercambios",
                defaultConfig.entryTtl(Duration.ofSeconds(intercambioTtl)),
                "tickets",
                defaultConfig.entryTtl(Duration.ofSeconds(ticketTtl))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(caches)
                .build();
    }
}
