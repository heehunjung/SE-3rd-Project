package com.seProject.stockTrading.domain.member;

import com.seProject.stockTrading.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;


public class PasswordTest {

    @Test
    void encodingTest() {
        // Config에 memberService
        ApplicationContext ac = new AnnotationConfigApplicationContext(SecurityConfig.class);
        PasswordEncoder encoder = ac.getBean(PasswordEncoder.class);

        String password = "password";

        String encodedPassword = encoder.encode(password);

        assertThat(encodedPassword).startsWith("{");
        assertThat(encodedPassword).contains("{bcrypt}");
        assertThat(encodedPassword).isNotEqualTo(password);
    }


}
