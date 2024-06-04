package com.example.processor;

import com.example.entity.Person;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

public class PersonProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(@NonNull Person person) throws Exception {

        // Add logic if we want to process certain data only like process if gender is Male
        if (person.getGender().equalsIgnoreCase("Male")) {
            return null;
        }
        return person;
    }
}
