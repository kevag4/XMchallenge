package com.kevag4.XMchallenge.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kevag4.XMchallenge.model.Crypto;
import com.kevag4.XMchallenge.model.CryptoSymbol;
import com.kevag4.XMchallenge.service.CryptoServiceImpl;

@RestController
@RequestMapping(path = "/api/cryptos")
public class CryptoRecommendationController {
    Logger logger = LoggerFactory.getLogger(CryptoRecommendationController.class);

    private CryptoServiceImpl cryptoService;

    @Autowired
    public CryptoRecommendationController(CryptoServiceImpl cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * Get all Cryptos.
     *
     * @param page
     * @param size
     * @returns information of the total pages, current page and the cryptos in the
     *          current page
     */
    @GetMapping
    public ResponseEntity<Page<Crypto>> getAllCryptos(Pageable page) {
        try {
            return new ResponseEntity<>(cryptoService.findAll(page.getPageNumber(), page.getPageSize()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get Crypto Details by type.
     *
     * @param userId
     * @returns Json String with User details
     */
    @GetMapping("/getCryptoDetails/{symbol}")
    public ResponseEntity<List<Crypto>> getCryptoDetails(@PathVariable("symbol") String symbol) {
        return new ResponseEntity<>(cryptoService.retrieveCryptoDetails(CryptoSymbol.valueOf(symbol)), HttpStatus.OK);
    }

}
