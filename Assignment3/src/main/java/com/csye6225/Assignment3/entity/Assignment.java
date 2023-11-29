package com.csye6225.Assignment3.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "assignment")
    private List<Submission> submissions;


}
