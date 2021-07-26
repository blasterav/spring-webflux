package com.phoosop.reactive.repository;

import com.phoosop.reactive.model.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, Integer> {

}
