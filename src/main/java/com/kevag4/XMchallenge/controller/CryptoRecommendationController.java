package com.kevag4.XMchallenge.controller;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kevag4.XMchallenge.model.Crypto;
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
     * @param userId
     * @returns Json String with Crypto details
     */
    @GetMapping
    public ResponseEntity<List<Crypto>> getAllUsers() {
        return new ResponseEntity<>(cryptoService.findAll(), HttpStatus.OK);
    }
    
}
