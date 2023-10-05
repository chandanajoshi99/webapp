package com.csye6225.Assignment3.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private LocalDateTime accountCreated;
    @Column
    private LocalDateTime accountUpdated;

}
