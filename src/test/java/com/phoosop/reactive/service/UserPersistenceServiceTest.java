package com.phoosop.reactive.service;

import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.repository.UserRepository;
import com.phoosop.reactive.service.persistence.UserPersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserPersistenceServiceTest {

    @InjectMocks
    private UserPersistenceService userPersistenceService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversionService conversionService;

    @Test
    public void testSave() {
        UserCommand userCommand = new UserCommand()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE)
                .setType(UserType.USER)
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1);

        UserEntity userEntity = new UserEntity()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1.getValue());

        Mockito.when(conversionService.convert(userCommand, UserEntity.class)).thenReturn(userEntity);
        Mockito.when(userRepository.save(userEntity)).thenReturn(Mono.just(userEntity));
        StepVerifier.create(userPersistenceService.save(userCommand))
                .expectNextMatches(result -> result.getAge().equals(1)
                        && result.getCardId().equals("card_id")
                        && result.getDateOfBirth().equals("27-11-1991")
                        && result.getStatus().equals(UserStatus.ACTIVE)
                        && result.getType().equals(UserType.USER)
                        && result.getFirstName().equals("firstName")
                        && result.getSecondName().equals("secondName")
                        && result.getId().equals(1L)
                        && result.getLevel().equals(UserLevel.LEVEL_1))
                .verifyComplete();
        Mockito.verify(userRepository, Mockito.times(1)).save(userEntity);
        Mockito.verify(conversionService, Mockito.times(1)).convert(userCommand, UserEntity.class);

    }

    @Test
    public void testFindById() {
        long id = 1L;
        UserEntity userEntity = new UserEntity()
                .setId(1L)
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1.getValue());

        UserCommand userCommand = new UserCommand()
                .setId(1L)
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE)
                .setType(UserType.USER)
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1);

        Mockito.when(userRepository.findById(id)).thenReturn(Mono.just(userEntity));
        Mockito.when(conversionService.convert(userEntity, UserCommand.class)).thenReturn(userCommand);

        StepVerifier.create(userPersistenceService.findById(id))
                .expectNextMatches(result -> result.getAge().equals(1)
                        && result.getCardId().equals("card_id")
                        && result.getDateOfBirth().equals("27-11-1991")
                        && result.getStatus().equals(UserStatus.ACTIVE)
                        && result.getType().equals(UserType.USER)
                        && result.getFirstName().equals("firstName")
                        && result.getSecondName().equals("secondName")
                        && result.getId().equals(1L)
                        && result.getLevel().equals(UserLevel.LEVEL_1))
                .verifyComplete();

        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(conversionService, Mockito.times(1)).convert(userEntity, UserCommand.class);

    }

    @Test
    public void testFindAll() {
        PageRequest pageRequest = PageRequest.of(1, 10);

        UserEntity userEntity = new UserEntity()
                .setId(1L)
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1.getValue());

        UserCommand userCommand = new UserCommand()
                .setId(1L)
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE)
                .setType(UserType.USER)
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1);

        Mockito.when(userRepository.count()).thenReturn(Mono.just(1L));
        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(userEntity));
        Mockito.when(conversionService.convert(userEntity, UserCommand.class)).thenReturn(userCommand);

        StepVerifier.create(userPersistenceService.findAll(pageRequest))
                .expectNextMatches(userCommandCustomPage -> {
                    UserCommand resultUserCommand = userCommandCustomPage.getContent().get(0);

                    return resultUserCommand.getAge().equals(1)
                            && resultUserCommand.getCardId().equals("card_id")
                            && resultUserCommand.getDateOfBirth().equals("27-11-1991")
                            && resultUserCommand.getStatus().equals(UserStatus.ACTIVE)
                            && resultUserCommand.getType().equals(UserType.USER)
                            && resultUserCommand.getFirstName().equals("firstName")
                            && resultUserCommand.getSecondName().equals("secondName")
                            && resultUserCommand.getId().equals(1L)
                            && resultUserCommand.getLevel().equals(UserLevel.LEVEL_1)
                            && userCommandCustomPage.getSize() == 1
                            && userCommandCustomPage.getContent().size() == 1
                            && userCommandCustomPage.getTotalElements() == 1
                            && userCommandCustomPage.getNumber() == 1;
                })
                .verifyComplete();
        Mockito.verify(userRepository, Mockito.times(1)).count();
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verify(conversionService, Mockito.times(1)).convert(userEntity, UserCommand.class);
    }

    @Test
    public void testDelete() {
        long id = 1L;
        userPersistenceService.delete(id);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(id);
    }
}