package com.seProject.stockTrading.domain.member;

import lombok.*;

public class MemberRequestDTO {
    // static inner class
    // DTO를 Inner class로 작성하는 것은 비슷한 클래스를
    // 한 곳에 모아둘 목적이지 객체로 사용할려는 목적이 아님
    // static 으로 선언하는 게 좋다.
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberForm{
        private String name;

        private String nickname;

        private String username;

        private String password;

        private Long number;
    }
}
