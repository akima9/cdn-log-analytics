package com.example.cdn.config

import com.example.cdn.mock.CloudFrontLogGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MockConfig {

    @Bean
    fun cloudFrontLogGenerator() = CloudFrontLogGenerator()
}
