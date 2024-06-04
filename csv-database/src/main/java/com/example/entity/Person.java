package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "person_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
}
