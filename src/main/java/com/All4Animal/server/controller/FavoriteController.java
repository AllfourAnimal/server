package com.All4Animal.server.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorite")
@Tag(name = "Favorite", description = " API")
@RequiredArgsConstructor
public class FavoriteController {

    @GetMapping("")
    public ResponseEntity<?> getExample(){
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
