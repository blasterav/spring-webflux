package com.phoosop.reactive.repository;

import com.phoosop.reactive.model.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface UserRepository extends ReactiveSortingRepository<UserEntity, Long> {

}
