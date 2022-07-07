package com.kevag4.XMchallenge.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kevag4.XMchallenge.model.BTCCrypto;
import com.kevag4.XMchallenge.model.Crypto;
import com.kevag4.XMchallenge.model.CryptoDetails;
import com.kevag4.XMchallenge.model.CryptoSymbol;
import com.kevag4.XMchallenge.model.DOGECrypto;
import com.kevag4.XMchallenge.model.ETHCrypto;
import com.kevag4.XMchallenge.model.LTCCrypto;
import com.kevag4.XMchallenge.model.XRPCrypto;
import com.kevag4.XMchallenge.repository.CryptoRepository;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final String FROM_FILTER_DATE = "2022-01-01";

    Logger logger = LoggerFactory.getLogger(CryptoServiceImpl.class);
    private CryptoRepository cryptoRepository;

    @Autowired
    public CryptoServiceImpl(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    @Override
    public Crypto createCrypto(Instant timestamp, CryptoSymbol symbol, BigDecimal price) {
        // depending on the crypto symbol the correct instance is created.
        Crypto crypto = null;
        switch (symbol) {
            case BTC:
                crypto = BTCCrypto.builder().timestamp(timestamp).price(price).build();
                break;
            case DOGE:
                crypto = DOGECrypto.builder().timestamp(timestamp).price(price).build();
                break;
            case ETH:
                crypto = ETHCrypto.builder().timestamp(timestamp).price(price).build();
                break;
            case LTC:
                crypto = LTCCrypto.builder().timestamp(timestamp).price(price).build();
                break;
            case XRP:
                crypto = XRPCrypto.builder().timestamp(timestamp).price(price).build();
                break;
            default:
                break;
        }
        logger.trace("Crypto with symbol: " + symbol.name() + " created.");
        logger.debug("Crypto: " + crypto.toString() + ", with symbol: " + crypto.getCryptoSymbol());
        return crypto;
    }

    @Override
    public List<Crypto> populateCryptosFromCsv(InputStream file) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Crypto> cryptos = new ArrayList<Crypto>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Crypto crypto = createCrypto(Instant.ofEpochMilli(Long.parseLong(csvRecord.get("timestamp"))),
                        CryptoSymbol.valueOf(csvRecord.get("symbol")),
                        new BigDecimal(csvRecord.get("price")));
                cryptos.add(crypto);
            }
            logger.trace("Finished parsing csv, saving in database..");
            cryptoRepository.saveAll(cryptos);
            return cryptos;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    @Override
    public Page<Crypto> findAll(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Crypto> pageCryptos = cryptoRepository.findAll(paging);
        logger.trace("Fetched page with cryptos inside findAll()");

        // return new PageImpl so to trick the Type erasure and include JsonTypeInfo
        // symbol in the serialization
        return new PageImpl<>(pageCryptos.getContent(), paging, pageCryptos.getTotalElements()) {
        };
    }

    @Override
    public CryptoDetails retrieveCryptoDetails(CryptoSymbol symbol, String fromDate, String toDate) {
        String fd = (fromDate == null || fromDate.isEmpty()) ? FROM_FILTER_DATE : fromDate;
        String td = (toDate == null || toDate.isEmpty()) ? LocalDate.now().toString() : toDate;

        Crypto olderEntry = cryptoRepository.retrieveOlderValuesBySymbol(symbol.name(), fd, td);
        Map<String, Object> olderValue = new HashMap<>();
        olderValue.put("price", olderEntry.getPrice());
        olderValue.put("timestamp", olderEntry.getTimestamp());
        Crypto newerEntry = cryptoRepository.retrieveNewerValuesBySymbol(symbol.name(), fd, td);
        Map<String, Object> newerValue = new HashMap<>();
        newerValue.put("price", newerEntry.getPrice());
        newerValue.put("timestamp", newerEntry.getTimestamp());

        logger.trace("Gathered all nessacary info, ready to build the CryptoDetails answer.");
        return CryptoDetails.builder().symbol(symbol)
                .min_value_price(cryptoRepository.min(symbol.name(), fd, td))
                .max_value_price(cryptoRepository.max(symbol.name(), fd, td))
                .older_value(olderValue)
                .newerValue(newerValue)
                .build();
    }

    @Override
    public Page<Crypto> getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(int page, int size, String fromDate,
            String toDate) {
        Pageable paging = PageRequest.of(page, size);

        // if user has not defined filtering dates, choose beginning of 2022 and/or now date
        Page<Crypto> pageCryptos = cryptoRepository.getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(
                (fromDate == null || fromDate.isEmpty()) ? FROM_FILTER_DATE : fromDate,
                (toDate == null || toDate.isEmpty()) ? LocalDate.now().toString() : toDate,
                paging);
        logger.trace("Fetched sorted page with cryptos inside getAllCryptosSortedByPriceAgainstNormalizedRangeDesc()");

        // return new PageImpl so to trick the Type erasure and include JsonTypeInfo
        // symbol in the serialization
        return new PageImpl<>(pageCryptos.getContent(), paging, pageCryptos.getTotalElements()) {
        };
    }

    @Override
    public Map<String, Object> getCryptoWithHighestNormalizedRangeForADay(String day) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal largestNormRange = BigDecimal.ZERO;
        CryptoSymbol symbOfLargerNorm = null;
        // iterate over norm ranges of all cryptos and keep the one with the highest
        for (CryptoSymbol symbol : CryptoSymbol.values()) {
            BigDecimal tempNormRange = cryptoRepository.getCryptoWithHighestNormalizedRangeForOneDay(symbol.name(), day);
            logger.debug("symbol: " + symbol.name() + ", tempNormRange: " + tempNormRange + ", largestNormRange: "
                    + largestNormRange);
            if (tempNormRange != null && tempNormRange.compareTo(largestNormRange) == 1) {
                largestNormRange = tempNormRange;
                symbOfLargerNorm = symbol;
            }
        }
        logger.trace("Gathered all information, preparing the result");
        result.put("crypto_symbol", symbOfLargerNorm);
        result.put("normalization_range", largestNormRange);
        result.put("day", day);
        return result;
    }
}
