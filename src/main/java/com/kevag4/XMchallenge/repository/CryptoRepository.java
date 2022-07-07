package com.kevag4.XMchallenge.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.kevag4.XMchallenge.model.Crypto;

@RepositoryRestResource(exported = false)
@Repository
public interface CryptoRepository extends CrudRepository<Crypto, Long> {

    Page<Crypto> findAll(Pageable paging);

    @Query(value = "select min(price) from crypto where symbol = :symbol and timestamp between :from and :to", nativeQuery = true)
    public BigDecimal min(@Param("symbol") String symbol, @Param("from") String from, @Param("to") String to);

    @Query(value = "select max(price) from crypto where symbol = :symbol and timestamp between :from and :to", nativeQuery = true)
    public BigDecimal max(@Param("symbol") String symbol, @Param("from") String from, @Param("to") String to);

    @Query(value = "select * from crypto where symbol = :symbol and timestamp between :from and :to group by id order by timestamp asc limit 1", nativeQuery = true)
    Crypto retrieveOlderValuesBySymbol(@Param("symbol") String symbol, @Param("from") String from, @Param("to") String to);

    @Query(value = "select * from crypto where symbol = :symbol and timestamp between :from and :to group by id order by timestamp desc limit 1", nativeQuery = true)
    Crypto retrieveNewerValuesBySymbol(@Param("symbol") String symbol, @Param("from") String from, @Param("to") String to);

    @Query(value = "select * from crypto where timestamp between :from and :to order by abs(price * (select (max(price)-min(price))/min(price) from crypto)) desc", nativeQuery = true)
    Page<Crypto> getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(@Param("from") String from, @Param("to") String to, Pageable paging);

    @Query(value = "select (max(price)-min(price))/min(price) as norm_range from crypto where symbol = :symbol and TIMESTAMPDIFF(DAY, DATE(timestamp), DATE(:day)) = 0", nativeQuery = true)
    BigDecimal getCryptoWithHighestNormalizedRangeForOneDay(@Param("symbol") String symbol, @Param("day") String day);
}