package com.oims.dto;

import java.math.BigDecimal;

public record TopProduct(Integer productId, String name, String sku, Long qtySold, BigDecimal revenue) {

    public Integer getProductId() { return productId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public Long getQtySold() { return qtySold; }
    public BigDecimal getRevenue() { return revenue; }
}
