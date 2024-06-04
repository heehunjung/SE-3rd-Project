package com.seProject.stockTrading.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberStockDTO {
    private Long stockId;
    private String stockName;
    private Long stockQuantity;
}
