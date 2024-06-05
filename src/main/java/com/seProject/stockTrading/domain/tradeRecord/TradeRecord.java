package com.seProject.stockTrading.domain.tradeRecord;

import com.seProject.stockTrading.domain.member.Member;
import com.seProject.stockTrading.domain.stock.Stock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class TradeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private int quantity;
    private float price;
    private String type; // BUY or SELL
    private Timestamp timestamp;
}
