package com.csye6225.Assignment3.services;

import com.csye6225.Assignment3.entity.Assignment;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface AssignmentService {
    void createAssignment(JsonNode reqNode);

    Assignment getOneAssignment(String id);

    List<Assignment> getAllAssignments();

    boolean deleteAssignment(String id);

    Assignment updateAssignment(String id, JsonNode body);

}
