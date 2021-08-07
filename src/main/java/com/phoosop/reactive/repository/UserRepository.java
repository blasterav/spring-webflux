package com.phoosop.reactive.repository;

import com.phoosop.reactive.model.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveSortingRepository<UserEntity, Long> {

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT :skip,:limit")
    Flux<UserEntity> findAll(@Param("skip") int page, @Param("limit") int limit);

    @Query("SELECT count(id) FROM user")
    Mono<Long> count();

}
