# Basic Spring WebFlux service

This repo is an example of Spring WebFlux Service with tools which are helpful to do the development

WebFlux runs on non-Servlet runtimes such as Netty and Undertow (non-blocking mode) as well as Servlet 3.1+ runtimes such as Tomcat and Jetty.

The Spring Boot WebFlux starter defaults to use Netty, but it is easy to switch by changing your Maven or Gradle dependencies.

Spring Boot provides auto-configuration for Spring WebFlux that works well for the common cases. If you want full control of the WebFlux configuration, the @EnableWebFlux annotation can be used (this annotation would also be needed in a plain Spring application to import the Spring WebFlux configuration).

## Structure

    HTTP ---> Controller ---> Component ---> PersistanceService ---> Repository ---> DB
                                        ---> ConnectorService ---> HTTP
                                        ---> UtilService ---> PersistanceService ---> Repository ---> DB
                                                         ---> ConnectorService ---> HTTP

## Dependencies

In this example we are using: 
* Java: 16
* Spring boot: 2.5.3
* r2dbc-mysql from miku: 0.8.2.RELEASE

## R2DBC

The Reactive Relational Database Connectivity (R2DBC) project brings reactive programming APIs to relational databases.

R2DBC is founded on the Reactive Streams specification, which provides a fully-reactive non-blocking API.

To be able to work with Mysql without blocking we need to use r2dbc-mysql. It provides to us possibility to work with database in reactive manner.

Spring Data provides to us almost the same experience as for Spring MVC. Below you can see an example:


    public interface UserRepository extends ReactiveSortingRepository<UserEntity, Long> {

        @Query("SELECT * FROM user ORDER BY id DESC LIMIT :skip,:limit")
        Flux<UserEntity> findAll(@Param("skip") int page, @Param("limit") int limit);
    
        @Query("SELECT count(id) FROM user")
        Mono<Long> count();

    }

[NOTE] *What i have noticed using default builder of queries reducing performance. That's why in this example we are using native queries*

## Controller

If you have worked with Spring MVC, the annotation-based model will look quite familiar for you since it is using the same annotations. 
The only difference is that the methods now return the reactive types Mono and Flux.

    @PostMapping(path = "/v1/users")
    public Mono<Response<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        Mono<UserResponse> userResponseMono = userComponent.createUser(request);
        return success(userResponseMono);
    }

## Exceptions

In Spring MVC we used to have `@ControllerAdvice` and `ResponseEntityExceptionHandler` but in Spring WebFlux we don't have it anymore.
To be able to handle exceptions globally we need to use `AbstractErrorWebExceptionHandler`. 

    @Component
    @Order(-2)
    public class CustomErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

        private final Logger LOG = LoggerFactory.getLogger(CustomErrorWebExceptionHandler.class);
    
        public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
            super(errorAttributes, new WebProperties.Resources(), applicationContext);
            super.setMessageWriters(serverCodecConfigurer.getWriters());
            super.setMessageReaders(serverCodecConfigurer.getReaders());
        }
    
        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
        }
    
        @Override
        protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
    
        }
    
        private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
            Throwable throwable = getError(request);
            if (throwable instanceof ServiceException exception) {
                LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));
            } 
            LOG.error("Failed {} {}: {}", request.methodName(), request.uri().getPath(), throwable.getMessage());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(HttpConstants.INTERNAL_SERVER_ERROR), null)));
            
        }
    }

[NOTE] *For full example please check the code*

## WebClient

Spring WebFlux also includes a reactive, fully non-blocking web client. It has a functional, fluent API based on Reactor.

First we need to init out web client:

    @Bean
    public WebClient boredapiWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        ConnectionProvider connectionProvider = ConnectionProvider.builder("boredapi")
                .maxConnections(500)
                .pendingAcquireMaxCount(1000)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        return WebClient.builder()
                .baseUrl(boredapiProperties.getHost())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }

[NOTE] *In this example we turned off validation of https*

[NOTE] *In this example we have set some basic configurations such as maxConnections, pendingAcquireMaxCount, maxIdleTime, maxLifeTime and pendingAcquireTimeout*

And now we can send request by using this client:

    public Mono<ActivityCommand> getActivity() {
        return boredapiWebClient.get()
                .uri(boredapiProperties.getPath().getGetActivity())
                .headers(headers -> headers.setBasicAuth("user", "userpwd"))
                .retrieve()
                .bodyToMono(ActivityExternal.class)
                .map(item -> conversionService.convert(item, ActivityCommand.class));
    }

## Blocking

How do we handle blocking calls? Reactor provides to us possibility to work with them by using `fromCallable` and put it to a different thread otherwise it will block the thread of event loop.

Here is an example of how to handle it:
    
    public Mono<String> generateRSA(Long id) {
        return userPersistenceService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(HttpConstants.USER_NOT_FOUND)))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userCommand -> Mono.fromCallable(CryptoUtils::generateKeyPair))
                .map(keyPair -> Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
    }

[NOTE] *In this example we are using Schedulers.boundedElastic() to be able to put it in a different thread*

## Filters

Filters help centralizing a common piece of functionalities that are required to process Web Requests. Such functionalities include – authentication and authorization, logging, or timeouts etc.

    @Component
    public class PrintLogsWebFilter implements WebFilter {

        private final Logger LOG = LoggerFactory.getLogger(PrintLogsWebFilter.class);
    
        @Override
        public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
            String path = serverWebExchange.getRequest().getURI().getPath();
            String method = serverWebExchange.getRequest().getMethod().name();
            long startTime = System.currentTimeMillis();
    
            LOG.info("Started {} {}", method, path);
            return webFilterChain.filter(serverWebExchange).doFinally(signalType -> {
                long totalTime = System.currentTimeMillis() - startTime;
                int statusCode = serverWebExchange.getResponse().getStatusCode().value();
                LOG.info("Finished {} {}, Status: {}, Duration: {}", method, path, statusCode, totalTime);
            });
        }
    }

## BlockHound

BlockHound is a java agent to detect blocking calls from non-blocking threads.

First we need to add dependency: 

    <dependency>
		<groupId>io.projectreactor.tools</groupId>
		<artifactId>blockhound-junit-platform</artifactId>
		<version>${io.projectreactor.tools.version}</version>
	</dependency>

And after just add `BlockHound.install();` to your Application

    @SpringBootApplication
    public class ReactiveApplication {

	public static void main(String[] args) {
		BlockHound.install();
		SpringApplication.run(ReactiveApplication.class, args);
	}

}

##Testing

Example of integration test(used to test controller):

    @ExtendWith(SpringExtension.class)
    @WebFluxTest(controllers = UserController.class)
    class UserControllerTest {
    
        @Autowired
        private WebTestClient webTestClient;
    
        @MockBean
        private UserComponent userComponent;
    
        @Test
        @DisplayName("Create user - success")
        public void testCreateUser_success() throws Exception {
            CreateUserRequest actualRequest = new CreateUserRequest()
                    .setCardId("cardId")
                    .setFirstName("firstName")
                    .setSecondName("secondName")
                    .setType(UserType.USER.getValue())
                    .setStatus(UserStatus.ACTIVE.getValue())
                    .setDateOfBirth("11-11-1991")
                    .setAge(18)
                    .setMobileNumber("12345678901")
                    .setMobileBrand("Apple");
    
            UserResponse userResponse = new UserResponse()
                    .setId(1L)
                    .setCardId("cardId")
                    .setFirstName("firstName")
                    .setSecondName("secondName")
                    .setType(UserType.USER.getValue())
                    .setStatus(UserStatus.ACTIVE.getValue())
                    .setLevel(UserLevel.LEVEL_1.getValue())
                    .setAge(18);
    
            Mockito.when(userComponent.createUser(Mockito.any(CreateUserRequest.class)))
                    .thenReturn(Mono.just(userResponse));
    
            ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
            };
            Response<UserResponse> actualResponse = webTestClient.post().uri("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(actualRequest))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(typeReference)
                    .returnResult()
                    .getResponseBody();
    
            Mockito.verify(userComponent, Mockito.times(1)).createUser(Mockito.any(CreateUserRequest.class));
    
            assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SUCCESS.getCode());
            assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SUCCESS.getDesc());
            assertThat(actualResponse.getData().getId()).isEqualTo(1L);
            assertThat(actualResponse.getData().getCardId()).isEqualTo("cardId");
            assertThat(actualResponse.getData().getFirstName()).isEqualTo("firstName");
            assertThat(actualResponse.getData().getSecondName()).isEqualTo("secondName");
            assertThat(actualResponse.getData().getType()).isEqualTo(UserType.USER.getValue());
            assertThat(actualResponse.getData().getStatus()).isEqualTo(UserStatus.ACTIVE.getValue());
            assertThat(actualResponse.getData().getLevel()).isEqualTo(UserLevel.LEVEL_1.getValue());
            assertThat(actualResponse.getData().getAge()).isEqualTo(18);
        }
    }

Example of unit test:

    @ExtendWith(MockitoExtension.class)
    class UserComponentTest {
    
        @InjectMocks
        private UserComponent userComponent;
    
        @Mock
        private ConversionService conversionService;
    
        @Mock
        private UserPersistenceService userPersistenceService;
    
        @Mock
        private BoredapiClientService boredapiClientService;
    
        @Test
        public void testCreateUser() {
            CreateUserRequest request = new CreateUserRequest();
            UserCommand returnConverterToUserCommand = new UserCommand();
            UserCommand returnPersistence = new UserCommand();
    
            UserResponse returnConverterToUserResponse = new UserResponse()
                    .setAge(1)
                    .setCardId("card_id")
                    .setDateOfBirth("27-11-1991")
                    .setStatus(UserStatus.ACTIVE.getValue())
                    .setType(UserType.USER.getValue())
                    .setFirstName("firstName")
                    .setSecondName("secondName")
                    .setId(1L)
                    .setLevel(UserLevel.LEVEL_1.getValue());
    
            Mockito.when(conversionService.convert(request, UserCommand.class)).thenReturn(returnConverterToUserCommand);
            Mockito.when(userPersistenceService.save(returnConverterToUserCommand)).thenReturn(Mono.just(returnPersistence));
            Mockito.when(conversionService.convert(returnPersistence, UserResponse.class)).thenReturn(returnConverterToUserResponse);
            StepVerifier.create(userComponent.createUser(request))
                    .expectNextMatches(result -> result.getAge().equals(1)
                            && result.getCardId().equals("card_id")
                            && result.getDateOfBirth().equals("27-11-1991")
                            && result.getStatus().equals(UserStatus.ACTIVE.getValue())
                            && result.getType().equals(UserType.USER.getValue())
                            && result.getFirstName().equals("firstName")
                            && result.getSecondName().equals("secondName")
                            && result.getId().equals(1L)
                            && result.getLevel().equals(UserLevel.LEVEL_1.getValue()))
                    .verifyComplete();
            Mockito.verify(conversionService, Mockito.times(1)).convert(request, UserCommand.class);
            Mockito.verify(userPersistenceService, Mockito.times(1)).save(returnConverterToUserCommand);
            Mockito.verify(conversionService, Mockito.times(1)).convert(returnPersistence, UserResponse.class);
        }
    }

## References

For more details please refer to:

* Projectreactor: https://projectreactor.io/docs/core/release/reference/

* WebFlux: https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html

* R2DBC: https://r2dbc.io/

* Spring Data R2DBC: https://docs.spring.io/spring-data/r2dbc/docs/1.2.6/reference/html/#get-started:first-steps:spring

* BlockHound: https://github.com/reactor/BlockHound

* Reactor Netty: https://projectreactor.io/docs/netty/release/reference/index.html

* Reactive Microservices: https://dzone.com/articles/spring-reactive-microservices-a-showcase