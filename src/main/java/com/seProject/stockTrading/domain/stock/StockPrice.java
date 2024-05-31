package com.seProject.stockTrading.domain.stock;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private float closingPrice;
    private float openPrice;
    private float highPrice;
    private float lowPrice;
    private int volume;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

}
