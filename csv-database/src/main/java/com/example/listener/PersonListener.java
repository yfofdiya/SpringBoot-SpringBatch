package com.example.listener;

import com.example.entity.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class PersonListener implements SkipListener<Person, Number> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.info("Failure occurred during read {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(@NonNull Number number, Throwable t) {
        log.info("Failure occurred during write {} , {}", t.getMessage(), number);
    }

    @SneakyThrows
    @Override
    public void onSkipInProcess(@NonNull Person person, Throwable t) {
        log.info("Invoice: {} was skipped due to the exception: {}", new ObjectMapper().writeValueAsString(person), t.getMessage());
    }
}
