package com.kevag4.XMchallenge.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.kevag4.XMchallenge.model.Crypto;
import com.kevag4.XMchallenge.model.CryptoSymbol;

public interface CryptoService {

    /**
     * Create a new Crypto Object.
     *
     * @param timestamp
     * @param symbol
     * @param price
     * @return Crypto Entity
     */
    public Crypto createCrypto(Instant timestamp, CryptoSymbol symbol, BigDecimal price);

    /**
     * Insert records from csv files to database.
     *
     * @param file
     * @return void or throws exception 
     */
    public List<Crypto> populateCryptosFromCsv(InputStream file) throws IOException;

    /**
     * Get all cryptos
     *
     * @return all cryptos
     */
    public List<Crypto> findAll();
}
