package com.seProject.stockTrading.domain.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seProject.stockTrading.global.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;


public class PasswordTest {

    @Test
    void encodingTest() {
        // Config에 memberService
        ApplicationContext ac = new AnnotationConfigApplicationContext(SecurityConfig.class,TestConfig.class);
        PasswordEncoder encoder = ac.getBean(PasswordEncoder.class);

        String password = "password";

        String encodedPassword = encoder.encode(password);

        assertThat(encodedPassword).startsWith("{");
        assertThat(encodedPassword).contains("{bcrypt}");
        assertThat(encodedPassword).isNotEqualTo(password);
    }

    @Test
    void 패스워드_랜덤_암호화() throws Exception {
        // Config에 memberService
        ApplicationContext ac = new AnnotationConfigApplicationContext(SecurityConfig.class,TestConfig.class);
        PasswordEncoder encoder = ac.getBean(PasswordEncoder.class);

        String password = "password";

        String encodePassword = encoder.encode(password);
        String encodePassword2 = encoder.encode(password);

        assertThat(encodePassword).isNotEqualTo(encodePassword2);
    }

    @Test
    void 패스워드_매치() throws Exception {
        // Config에 memberService
        ApplicationContext ac = new AnnotationConfigApplicationContext(SecurityConfig.class,TestConfig.class);
        PasswordEncoder encoder = ac.getBean(PasswordEncoder.class);

        String password = "password";
        String encodePassword = encoder.encode(password);
        assertThat(encoder.matches(password, encodePassword)).isTrue();

    }
    // memberService의 구현체를 대충 넣어줄거임
    // 근데 결론적으론 분리하는게 나은가 원래 이런식으로 하나 모르겟네
    static class TestConfig {
        @Bean
        public MemberService memberService() {
            return new mockImpl();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    static class mockImpl implements MemberService {

        @Override
        public Member CreateMember(MemberRequestDTO.MemberForm memberForm, String encodedPassword) {
            return null;
        }

        @Override
        public boolean checkUsername(String username) {
            return false;
        }

        @Override
        public boolean checkPerson(String name, Long number) {
            return false;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return null;
        }
    }
}
