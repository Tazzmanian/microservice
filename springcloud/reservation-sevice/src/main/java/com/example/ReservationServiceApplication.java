package com.example;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;

@SpringBootApplication
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
