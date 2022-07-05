package com.kevag4.XMchallenge.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.kevag4.XMchallenge.model.Crypto;

@RepositoryRestResource(exported = false)
@Repository
public interface CryptoRepository extends CrudRepository<Crypto, Long> {

}