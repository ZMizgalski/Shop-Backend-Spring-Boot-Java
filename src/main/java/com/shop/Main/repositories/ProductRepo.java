package com.shop.Main.repositories;

import com.shop.Main.models.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepo extends MongoRepository<Products, String> {

    Boolean existsByProductName(String name);

    Page<Products> findByProductNameLikeOrDescriptionLikeOrProductType(String productName,String description,String productType, Pageable pageable);

    Page<Products> findAll(Pageable pageable);

    List<Products> findByProductNameLike(String productName);
    List<Products> findByDescriptionLike(String description);
    List<Products> findByProductTypeLike(String productType);
}
