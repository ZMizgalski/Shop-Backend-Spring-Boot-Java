package com.shop.Main.repositories;

import com.shop.Main.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepo extends MongoRepository<Token, String> {

    Token findByToken(String token);

    void deleteByEmail(String email);

    Boolean existsByEmail(String email);

    Token findByEmail(String email);
}
