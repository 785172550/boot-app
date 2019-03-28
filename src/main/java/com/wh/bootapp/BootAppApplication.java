package com.wh.bootapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @Author kenneth
 * @Date 12:21 AM 3/29/2019
 * @Description //TODO BootAppApplication
 *
 */
@Slf4j
@SpringBootApplication
public class BootAppApplication {


    /**
     * security
     *
     * @return MapReactiveUserDetailsService
     */
//    @Bean
//    MapReactiveUserDetailsService reactiveUserDetailsService() {
//        return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
//                .username("test").password("pw").roles("USER").build());
//    }

    /**
     * actuator: access url: actuator/health
     *
     * @return HealthIndicator
     */
    @Bean
    public HealthIndicator healthIndicator() {
        return () -> Health.status("I < 3 ok").build();
    }


    @Bean
    public RouterFunction<ServerResponse> routes(CustomerRepo repo, CustomerHandler handler) {
        return RouterFunctions
                .route(GET("/customer"), serverRequest -> ok().body(repo.findAll(), Customer.class))
                .andRoute(POST("/customer"), handler::create)
                .andRoute(DELETE("/customer/{id}"),
                        serverRequest -> ok().build(repo.deleteById(serverRequest.pathVariable("id"))));
    }


    public static void main(String[] args) {
        SpringApplication.run(BootAppApplication.class, args);
    }

}


@Component
class DataWriter implements ApplicationRunner {

    private CustomerRepo repo;

    public DataWriter(CustomerRepo repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Flux.just("kenenth", "dragon", "cloud", "lee")
//                .flatMap(name -> repo.save(new Customer(null, name))) //async
//                .subscribe(System.out::println);

    }
}

interface CustomerRepo extends ReactiveMongoRepository<Customer, String> {

}

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
class Customer {
    private String id, name;
}

@Slf4j
@Component
class CustomerHandler {

    @Autowired
    private CustomerRepo repo;

//    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<Customer> customer = request.bodyToFlux(Customer.class);
        Flux<Customer> res = customer.log().buffer(10000).flatMap(items -> repo.saveAll(items));
        return ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(res, Customer.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("id is - {}", id);
        return ok().build(repo.deleteById(id));
    }
}

