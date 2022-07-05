package com.kevag4.XMchallenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ETH")
@SuperBuilder
@Data
@NoArgsConstructor
public class ETHCrypto extends Crypto {

    @JsonIgnore
    @Override
    public CryptoSymbol getCryptoSymbol() {
        return CryptoSymbol.ETH;
    }
}