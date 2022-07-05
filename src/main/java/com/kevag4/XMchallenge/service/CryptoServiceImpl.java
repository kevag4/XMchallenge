package com.kevag4.XMchallenge.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
import com.kevag4.XMchallenge.model.CryptoSymbol;
import com.kevag4.XMchallenge.model.DOGECrypto;
import com.kevag4.XMchallenge.model.ETHCrypto;
import com.kevag4.XMchallenge.model.LTCCrypto;
import com.kevag4.XMchallenge.model.XRPCrypto;
import com.kevag4.XMchallenge.repository.CryptoRepository;

@Service
public class CryptoServiceImpl implements CryptoService {

    Logger logger = LoggerFactory.getLogger(CryptoServiceImpl.class);
    private CryptoRepository cryptoRepository;

    @Autowired
    public CryptoServiceImpl(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    @Override
    public Crypto createCrypto(Instant timestamp, CryptoSymbol symbol, BigDecimal price) {
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
        return new PageImpl<>(pageCryptos.getContent(), paging, pageCryptos.getTotalElements()) {};
    }

    @Override
    public List<Crypto> retrieveCryptoDetails(CryptoSymbol symbol) {
        logger.info(cryptoRepository.retrieveCryptoDetails("BTC").toString());
        return null;
    }
}
