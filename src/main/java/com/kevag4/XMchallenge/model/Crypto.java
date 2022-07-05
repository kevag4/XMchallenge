package com.kevag4.XMchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "symbol", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "symbol")
@JsonSubTypes({@JsonSubTypes.Type(value = BTCCrypto.class, name = "BTC"),
        @JsonSubTypes.Type(value = DOGECrypto.class, name = "DOGE"),
        @JsonSubTypes.Type(value = ETHCrypto.class, name = "ETH"),
        @JsonSubTypes.Type(value = LTCCrypto.class, name = "LTC"),
        @JsonSubTypes.Type(value = XRPCrypto.class, name = "XRP")
})
@SuperBuilder
@Data
@NoArgsConstructor
public abstract class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Long id;
    
    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private BigDecimal price;

    public abstract CryptoSymbol getCryptoSymbol();
}

