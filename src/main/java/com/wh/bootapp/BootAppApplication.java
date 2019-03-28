package com.wh.bootapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class BootAppApplication {

    /**
     *  security
     * @return
     */
    @Bean
    MapReactiveUserDetailsService reactiveUserDetailsService() {
        return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
                .username("test").password("pw").roles("USER").build());
    }


    /**
     * actuatot
     *
     * @return
     */
    @Bean
    public HealthIndicator healthIndicator() {
        return () -> Health.status("I < 3 ok").build();
    }

    @Bean
    public RouterFunction<ServerResponse> routes(CustomerRepo repo) {
        return RouterFunctions.route(GET("/customer"),
                serverRequest -> ok().body(repo.findAll(), Customer.class));
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
        Flux.just("kenenth", "dragon", "cloud", "lee")
                .flatMap(name -> repo.save(new Customer(null, name))) //async
                .subscribe(System.out::println);

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

