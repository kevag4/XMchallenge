package com.kevag4.XMchallenge.model;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CryptoDetails {

    private CryptoSymbol symbol;
    private BigDecimal min_value_price;
    private BigDecimal max_value_price;

    private Map<String, Object> older_value;
    private Map<String, Object> newerValue;
}
