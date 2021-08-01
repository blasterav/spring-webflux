package com.phoosop.reactive.service;

import com.phoosop.reactive.model.CustomPage;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import com.phoosop.reactive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPersistenceService {

    private final UserRepository userRepository;
    private final ConversionService conversionService;

    public Mono<UserCommand> save(UserCommand userCommand) {
        UserEntity userEntity = conversionService.convert(userCommand, UserEntity.class);
        return userRepository.save(userEntity)
                .map(item -> {
                    userCommand.setId(item.getId());
                    return userCommand;
                });
    }

    public Mono<UserCommand> findById(long id) {
        return userRepository.findById(id)
                .map(item -> conversionService.convert(item, UserCommand.class));
    }

    public Mono<CustomPage<UserCommand>> findAll(Pageable pageable) {
        Mono<Long> countMono = userRepository.count();
        long skip = pageable.getPageNumber() == 1 ? 0 : (long) pageable.getPageNumber() * pageable.getPageSize();
        Mono<List<UserCommand>> userCommandFlux = userRepository.findAll()
                .skip(skip)
                .take(pageable.getPageSize())
                .map(item -> conversionService.convert(item, UserCommand.class))
                .collectList();

        return Mono.zip(countMono, userCommandFlux, (aLong, list) -> new CustomPage<UserCommand>()
                .setContent(list)
                .setNumber(pageable.getPageNumber())
                .setSize(list.size())
                .setTotalElements(aLong));
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

}
