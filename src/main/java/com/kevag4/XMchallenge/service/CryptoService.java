package com.kevag4.XMchallenge.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.kevag4.XMchallenge.model.Crypto;
import com.kevag4.XMchallenge.model.CryptoDetails;
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
     * @param page
     * @param size
     * @return all cryptos
     */
    public Page<Crypto> findAll(int page, int size);

    /**
     * Calculates oldest/newest/min/max for each crypto
     * @param symbol
     * @return return the oldest/newest/min/max values for a requested crypto
     */
    public CryptoDetails retrieveCryptoDetails(CryptoSymbol symbol, String fromDate, String toDate);

    /**
     * Get all cryptos Sorted By Price Against Normalized Range Desc
     * Optional filtering for fetching results for specific month and year
     * @param page
     * @param size
     * @return a descending sorted list of all the cryptos, comparing the normalized range ((max-min)/min)
     */
    public Page<Crypto> getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(int page, int size, String fromDate, String toDate);

    /**
     * Get crypto with the highest normalized range for a specific day
     * @param day
     * @return the symbol of the crypto with the highest normalized range for a specific day together with the norm range
     */
    public Map<String, Object> getCryptoWithHighestNormalizedRangeForADay(String day);
}
