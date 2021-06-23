package com.shop.Main.repositories;

import com.shop.Main.models.ERoles;
import com.shop.Main.models.Roles;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepo extends MongoRepository<Roles, String> {

    Optional<Roles> findByName(ERoles name);
}
