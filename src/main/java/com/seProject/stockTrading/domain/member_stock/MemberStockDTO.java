package com.seProject.stockTrading.domain.member_stock;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberStockDTO {
    private Long stockId;
    private String stockName;
    private Long stockQuantity;
}
