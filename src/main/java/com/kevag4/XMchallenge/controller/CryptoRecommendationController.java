package com.kevag4.XMchallenge.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.kevag4.XMchallenge.model.Crypto;
import com.kevag4.XMchallenge.model.CryptoDetails;
import com.kevag4.XMchallenge.model.CryptoSymbol;
import com.kevag4.XMchallenge.service.CryptoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(path = "/api/cryptos")
@Validated
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
    @Operation(summary = "Get all Cryptos")
    @GetMapping
    public ResponseEntity<Page<Crypto>> getAllCryptos(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.trace("Received request for getAllCryptos()");
            return new ResponseEntity<>(cryptoService.findAll(page, size), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get Crypto Details by type.
     *
     * @param userId
     * @returns endpoint that returns the oldest/newest/min/max values for a
     *          requested crypto
     */
    @Operation(summary = "Get oldest/newest/min/max values for a requested crypto."
            + "Optional ability to filter with specific dates (default filtering from 2022 start until now)")
    @GetMapping("/getCryptoDetails/{symbol}")
    public ResponseEntity<CryptoDetails> getCryptoDetails(@PathVariable("symbol") @Validated CryptoSymbol symbol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> toDate) {
        try {
            logger.trace("Received request for getAllCryptos()");
            return new ResponseEntity<>(cryptoService.retrieveCryptoDetails(
                    symbol,
                    fromDate.isPresent() ? fromDate.get().toString() : null,
                    toDate.isPresent() ? toDate.get().toString() : null),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all Cryptos.
     *
     * @param page
     * @param size
     * @returns endpoint that returns a descending sorted list of all the cryptos,
     *          comparing the normalized range ((max-min)/min)
     */
    @Operation(summary = "Get a descending sorted list of all the cryptos, comparing the normalized range ((max-min)/min). "
            + "Optional ability to filter with specific dates (default filtering from 2022 start until now)")
    @GetMapping("/getAllCryptosSortedByPriceAgainstNormalizedRangeDesc")
    public ResponseEntity<Page<Crypto>> getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> toDate) {
        try {
            logger.trace("Received request for getAllCryptos()");
            return new ResponseEntity<>(cryptoService.getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(
                    page,
                    size,
                    fromDate.isPresent() ? fromDate.get().toString() : null,
                    toDate.isPresent() ? toDate.get().toString() : null),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get crypto with the highest normalized range for a specific day.
     *
     * @param day
     * @returns endpoint that will return the crypto with the highest normalized
     *          range for a specific day
     */
    @Operation(summary = "Get crypto with the highest normalized range for a specific day (yyyy-MM-dd).")
    @GetMapping("/getCryptoWithHighestNormalizedRangeForADay")
    public ResponseEntity<Map<String, Object>> getCryptoWithHighestNormalizedRangeForADay(@RequestParam("day") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        try {
            logger.trace("Received request for getAllCryptos()");
            return new ResponseEntity<>(cryptoService.getCryptoWithHighestNormalizedRangeForADay(day.toString()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Exception handler if InvalidDataAccessResourceUsageException is thrown in this Controller
     *
     * @param ex exception
     * @return Error message String.
     */
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<Map<String, Object>> return500(InvalidDataAccessResourceUsageException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("error_message", ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Exception handler if MethodArgumentTypeMismatchException is thrown in this Controller
     *
     * @param ex exception
     * @return Error message String.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> return500(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("error_message", ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
