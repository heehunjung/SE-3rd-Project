package com.seProject.stockTrading.domain.member_stock;

import com.seProject.stockTrading.domain.member.Member;
import com.seProject.stockTrading.domain.stock.Stock;
import jakarta.persistence.*;

@Entity
public class MemberStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 선호 종목
    private int isPreferred=0;
    // 구매 수량
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
}
