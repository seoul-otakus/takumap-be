package com.seoulotakus.takumapbe;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@ActiveProfiles("test")
class TakumapBeApplicationTests {

    @TestConfiguration
    static class TestS3Config {

        @Bean
        public S3Client s3Client() {

            return Mockito.mock(S3Client.class);
        }
    }

    @Test
    void contextLoads() {
    }

}