package com.kevag4.XMchallenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DOGE")
@SuperBuilder
@NoArgsConstructor
public class DOGECrypto extends Crypto {

    @JsonIgnore
    @Override
    public CryptoSymbol getCryptoSymbol() {
        return CryptoSymbol.DOGE;
    }
}