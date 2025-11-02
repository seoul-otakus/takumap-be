package com.seoulotakus.takumapbe;

import com.seoulotakus.takumapbe.global.config.oauth.PrincipalOauth2UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class TakumapBeApplicationTests {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public S3Client s3Client() {
            return Mockito.mock(S3Client.class);
        }

        @Bean
        public AuditorAware<Long> auditorAware() {
            return () -> Optional.empty();
        }
    }

    @Test
    void contextLoads() {
    }

}