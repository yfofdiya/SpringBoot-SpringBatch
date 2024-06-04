package com.practice.service;

import com.practice.entity.Person;
import com.practice.helper.ExcelHelper;
import com.practice.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public String save(MultipartFile file) {
        boolean result = ExcelHelper.checkExcelFormat(file);
        String message = "";
        if (result) {
            try {
                List<Person> personList = ExcelHelper.convertExcelToList(file.getInputStream());
                personRepository.saveAll(personList);
                message = "Data imported successfully";
            } catch (IOException e) {
                log.error("Something went wrong while converting excel to list : {}", e.getMessage());
                message = "Something wen wrong while converting excel to list";
            }
        } else {
            message = "File format is not valid, Please check and try again";
        }
        return message;
    }

    public List<Person> getAllPerson() {
        return personRepository.findAll();
    }
}
