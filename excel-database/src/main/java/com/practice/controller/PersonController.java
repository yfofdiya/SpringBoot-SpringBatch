package com.practice.controller;

import com.practice.service.PersonService;
import com.practice.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return new ResponseEntity<>(ApiResponse
                .builder()
                .data(personService.getAllPerson())
                .message("Fetched all persons")
                .build(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> importDataFromExcelToDatabase(@RequestParam("file") MultipartFile file) {
        String message = personService.save(file);
        return new ResponseEntity<>(ApiResponse
                .builder()
                .data("")
                .message(message)
                .build(), HttpStatus.OK);
    }
}
