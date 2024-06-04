package com.example.controller;

import com.example.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/persons")
@Slf4j
public class PersonController {

    @Autowired
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @GetMapping
    public ResponseEntity<ApiResponse> importDataFromCSVToDatabase() {
        ApiResponse apiResponse;

        JobParameters jobParams = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            long startTime = System.currentTimeMillis();
            jobLauncher.run(job, jobParams);
            long endTime = System.currentTimeMillis();
            long totalTimeMilliseconds = endTime - startTime;
            double totalTimeSeconds = (double) totalTimeMilliseconds / 1000;
            apiResponse = ApiResponse.builder().data(totalTimeSeconds).message("Successfully imported data").build();
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            log.error("Something went wrong while importing data : {}", e.getMessage());
            apiResponse = ApiResponse.builder().data(null).message(e.getMessage()).build();
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
