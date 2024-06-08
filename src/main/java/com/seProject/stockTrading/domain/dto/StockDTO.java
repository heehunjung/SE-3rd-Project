package com.seProject.stockTrading.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDTO {
    private Long stockId;
    private String stockName;
    private String stockSymbol;
    private Long like;
    private String content;
    public StockDTO(Long stockId,String stockName,String stockSymbol,Long like,String content) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.stockSymbol = stockSymbol;
        this.like = like;
        this.content = content;
    }

}
