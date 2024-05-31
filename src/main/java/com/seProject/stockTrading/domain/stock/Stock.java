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
    private String stockSymbol;
    private String stockName;
    private float currentPrice;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
}
