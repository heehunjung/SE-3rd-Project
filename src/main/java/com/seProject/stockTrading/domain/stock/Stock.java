package com.seProject.stockTrading.domain.stock;

import com.seProject.stockTrading.domain.stockPrice.StockPrice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
