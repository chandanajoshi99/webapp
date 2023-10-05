package com.csye6225.Assignment3.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Assignment")
public class Assignment {

    @Column
    private String name;
    @Column
    private int points;
    @Column
    private int number_of_Attempts;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    @JsonFormat(pattern="YYYY-MM-DD'T'HH:MM:SS.SSS'Z'",shape = JsonFormat.Shape.STRING, locale = "en_GB")
    private LocalDateTime deadline;
    @Column
    private LocalDateTime assignmentCreated;
    @Column
    private LocalDateTime assignmentUpdated;
    @Column
    private String ownerEmail;

}
