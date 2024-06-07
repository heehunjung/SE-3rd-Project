package com.seProject.stockTrading.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberPostDTO {
    private Long postId;
    private String username;
    private String title;
    private String nickname;
    public MemberPostDTO(String title, String username,String nickname,Long postId) {
        this.title = title;
        this.username = username;
        this.nickname = nickname;
        this.postId = postId;
    }

}
