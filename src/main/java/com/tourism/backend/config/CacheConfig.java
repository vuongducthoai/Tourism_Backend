package com.tourism.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "categories",           // Cache cho categories
                "popularCategories",    // Cache cho popular categories
                "tags",                 // Cache cho tags
                "popularTags",          // Cache cho popular tags
                "tagsByCategory"        // Cache cho tags by category
        );
    }
}