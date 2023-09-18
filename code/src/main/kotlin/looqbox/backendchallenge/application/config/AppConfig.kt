package looqbox.backendchallenge.application.config

import looqbox.backendchallenge.common.cache.CacheConfiguration
import looqbox.backendchallenge.resources.external.api.RestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun cacheConfiguration(): CacheConfiguration {
        return CacheConfiguration()
    }

    @Bean
    fun restConfiguration(): RestConfiguration {
        return RestConfiguration()
    }
}