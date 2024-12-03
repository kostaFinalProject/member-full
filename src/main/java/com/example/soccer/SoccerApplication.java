package com.example.soccer;

import com.example.soccer.config.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // JPA의 Audit 기능을 활성화하는 데 사용
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class) // 토큰 어노테이션
public class SoccerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoccerApplication.class, args);
	}

}
