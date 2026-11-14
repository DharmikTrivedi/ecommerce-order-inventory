package com.oims.dto;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public record MonthlySales(Integer year, Integer month, Long orderCount, BigDecimal revenue) {

    public String getLabel() {
        return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
    }

    // getters so JSP EL can read the fields
    public Integer getYear() { return year; }
    public Integer getMonth() { return month; }
    public Long getOrderCount() { return orderCount; }
    public BigDecimal getRevenue() { return revenue; }
}
