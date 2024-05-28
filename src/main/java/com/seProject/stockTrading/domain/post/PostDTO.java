package com.seProject.stockTrading.domain.post;

import lombok.Getter;

@Getter
public class PostDTO {
    private String content;
    private String title;
    private Long member_id;
    private int board;
}
