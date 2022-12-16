package org.example.repositories;


import org.example.model.ProductInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableScan
public interface ProductInfoRepository extends CrudRepository<ProductInfo, String> {
    Optional<ProductInfo> findById(String id);
}