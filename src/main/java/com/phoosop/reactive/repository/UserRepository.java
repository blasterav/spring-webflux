package com.phoosop.reactive.repository;

import com.phoosop.reactive.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<Page<UserEntity>> findAll(Pageable pageable);

}
