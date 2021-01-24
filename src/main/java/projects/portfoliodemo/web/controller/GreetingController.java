package projects.portfoliodemo.web.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projects.portfoliodemo.domain.model.Greeting;


import javax.websocket.server.PathParam;
import java.util.concurrent.atomic.AtomicLong;
@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    @GetMapping("/greeting2/{name}")
    public String greeting2(@PathVariable(value = "name") String name){
        return String.format(template, name);
    }



}
