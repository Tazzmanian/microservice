package com.example;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@IntegrationComponentScan
@SpringBootApplication
@EnableBinding(Sink.class)
public class ReservationServiceApplication {

    @Bean
    CommandLineRunner commandLineRunner(ReservationRepository reservationRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                Stream.of("Josh", "Pieter", "Tash", "Susie", "Max")
                        .forEach(n -> reservationRepository.save(new Reservation(n)));
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

@MessageEndpoint
class ReservationProcessor {

    @Autowired
    private ReservationRepository reservationRepository;

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void accpetNewReservation(String rn) {
        this.reservationRepository.save(new Reservation(rn));
    }
}

@RefreshScope
@RestController
class MessageRestController {

    @Value("${message}")
    private String msg;

    @RequestMapping("/message")
    String message() {
        return this.msg;
    }
}

@Entity
@Data
class Reservation {

    @Id
    @GeneratedValue
    private Long id;
    private String reservationName;

    public Reservation(String r) {
        this.reservationName = r;
    }

    public Reservation() {
    }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", reservationName=" + reservationName + "}";
    }
}

//@Component
//class DummyDataCLR implements CommandLineRunner {
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Override
//    public void run(String... strings) throws Exception {
//        Stream.of("Josh", "Pieter", "Tash", "Susie", "Max")
//                .forEach(n -> this.reservationRepository.save(new Reservation(n)));
//    }
//}
@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @RestResource(path = "by-name")
    Collection<Reservation> findByReservationName(String rn);
}
