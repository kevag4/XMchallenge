package com.kevag4.XMchallenge.repository;

import java.math.BigDecimal;

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

    @Query(value = "select min(price) from crypto where symbol = ?1", nativeQuery = true)
	public BigDecimal min(String symbol);

	@Query(value = "select max(price) from crypto where symbol = ?1", nativeQuery = true)
	public BigDecimal max(String symbol);

    @Query(value = "select * from crypto where symbol = ?1 group by id order by timestamp asc limit 1", nativeQuery = true)
    Crypto retrieveOlderValuesBySymbol(String symbol);

    @Query(value = "select * from crypto where symbol = ?1 group by id order by timestamp desc limit 1", nativeQuery = true)
    Crypto retrieveNewerValuesBySymbol(String symbol);

    @Query(value = "select * from crypto order by abs(price - (select (max(price)-min(price))/min(price) from crypto)) desc", nativeQuery = true)
    Page<Crypto> getAllCryptosSortedByPriceAgainstNormalizedRangeDesc(Pageable paging);

    @Query(value = "select (max(price)-min(price))/min(price) as norm_range from crypto where symbol = ?1 and TIMESTAMPDIFF(DAY, DATE(timestamp), DATE(?2)) = 0", nativeQuery = true)
    BigDecimal getCryptoWithHighestNormalizedRangeForADay(String symbol, String day);
}   