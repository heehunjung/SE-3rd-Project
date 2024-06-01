package com.seProject.stockTrading.domain.stock;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

import java.util.List;

@Entity
@Getter
@Setter
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //종목 번호
    private String stockSymbol;
    //주식 이름
    private String stockName;
    //현재 가격
    private float currentPrice;
    //관심 수
    @Column(nullable = false)
    private int interest = 0;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
}
