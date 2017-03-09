package com.example;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class ReservationClientApplication {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }
}

@RestController
@RequestMapping("/reservations")
class ReservationApiGetwayRestController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/names")
    public Collection<String> getReservationNames() {

        ParameterizedTypeReference<Resources<Reservation>> ptr = new ParameterizedTypeReference<Resources<Reservation>>() {
        };

        //return this.restTemplate.execute("http://reservation-service/reservations", HttpMethod.GET, null, ptr);
        //http://reservation-service/reservations is no good
        //http://localhost:8001/reservations works 
        //http://desktop-24v6k91:8001/reservations
        // should make the host name the same as the service name
        ResponseEntity<Resources<Reservation>> entity;
        entity = restTemplate.exchange("http://desktop-24v6k91:8001/reservations", HttpMethod.GET, null, ptr, "");

        return entity
                .getBody()
                .getContent()
                .stream()
                .map(Reservation::getReservationName)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/name")
    public String getReservationName() {
        return "Name";
    }
}

class Reservation {

    private String reservationName;

    public String getReservationName() {
        return reservationName;
    }

}
