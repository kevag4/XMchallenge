package com.kevag4.XMchallenge.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.kevag4.XMchallenge.model.Crypto;

@RepositoryRestResource(exported = false)
@Repository
public interface CryptoRepository extends CrudRepository<Crypto, Long> {
    
    Page<Crypto> findAll(Pageable paging);

    @Query(value = "select min(price) as min_price, max(price) as max_price, min(timestamp) as min_timestamp, max(timestamp) as max_timestamp from crypto cryptos where cryptos.symbol = ?1", nativeQuery = true)
    List<String> retrieveCryptoDetails(String symbol);
}   