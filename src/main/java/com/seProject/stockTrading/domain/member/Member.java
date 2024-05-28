package com.seProject.stockTrading.domain.member;

import com.seProject.stockTrading.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //비밀번호
    private Long password;
    //아이디
    private String username;
    //닉네임
    private String nickname;
    //사용자 이름
    private String name;
    //사용자 전화번호
    private Long number;
    //사용자 잔고
    @Column(nullable = false)
    private int balance = 0;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

}
