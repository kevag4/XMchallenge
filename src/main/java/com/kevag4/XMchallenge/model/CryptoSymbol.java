package com.kevag4.XMchallenge.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CryptoSymbol {
    BTC, DOGE, ETH, LTC, XRP
}