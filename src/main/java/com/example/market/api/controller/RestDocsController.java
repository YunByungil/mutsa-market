package com.example.market.api.controller;

import com.example.market.dto.RestDocsDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestDocsController {

    @GetMapping("/restdocs")
    public String restdocs(@RequestBody RestDocsDto dto) {

        return dto.getContent();
    }
}
