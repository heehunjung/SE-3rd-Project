package com.seProject.stockTrading.domain.post;
import com.seProject.stockTrading.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //게시글 제목
    private String title;
    //게시글 내용
    private String content;
    //조회수
    private int view;
    //게시판 종류 0: 전체, 1: 공지, 2: 종토방, 3: 자유 게시판
    private int board;
    //게시글 작성자 ㅡㅁ
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    // fk는 target의 pk로 자동으로 연결되나 ?
}