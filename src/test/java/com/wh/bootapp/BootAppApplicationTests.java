package com.wh.bootapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BootAppApplicationTests {

    private Logger LOGGER = LoggerFactory.getLogger(BootAppApplicationTests.class);

    @Test
    public void contextLoads() {
    }

    @Test
    public void testFaltMap() throws InterruptedException {
        Flux.just(1, 2, 3, 4)
                .log()
                .flatMap(e -> {
                    return Flux.just(e * 2).delayElements(Duration.ofSeconds(1));
                })
                .subscribe(e -> LOGGER.info("get:{}", e));
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void testMap() throws InterruptedException {
        Flux.just(1, 2, 3, 4)
                .log()
                .map(i -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return i * 2;
                })
                .subscribe(e -> LOGGER.info("get:{}", e));
    }
}
