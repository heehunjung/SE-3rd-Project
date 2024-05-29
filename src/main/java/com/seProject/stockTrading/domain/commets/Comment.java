package com.seProject.stockTrading.domain.commets;

import com.seProject.stockTrading.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //작성자 닉네임
    private String nickname;
    //해당 게시글 id
    private Long postId;
    //댓글 내용
    private String content;
    //작성 시간
    private Timestamp createdAt;
}
