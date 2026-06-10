package com.erikjarquin.ventas.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cobro")
public class CobroController {
    private final Map<Long, CobroItem> cobro = new HashMap<>();

    @GetMapping
    public Cobro getCobro(){
        return new Cobro(new ArrayList<>(cobro.values()));
    }

    
}
