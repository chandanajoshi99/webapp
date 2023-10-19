package com.csye6225.Assignment3.controller;

import com.csye6225.Assignment3.entity.Assignment;
import com.csye6225.Assignment3.services.AssignmentService;
import com.csye6225.Assignment3.services.JSONValidatorService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@Slf4j
public class AssignmentController {

    private static final String SCHEMA_PATH = "static/schema.json";
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private JSONValidatorService JSONValidatorService;

    @GetMapping("/healthz")
    public ResponseEntity<Void> databaseConnector(@RequestBody(required = false) String reqStr, @RequestParam(required = false) String reqPara) {
       try{
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("X-Content-Type-Options", "no-sniff")
                        .build();
            }
       catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "no-sniff")
                    .build();
        }
    }


    @GetMapping("/v1/assignments")
    public ResponseEntity<Object> getAllAssignments(@RequestBody(required = false) String reqStr, @RequestParam(required = false) String reqPara){
        if (reqStr != null || reqPara !=null){

            return ResponseEntity.status(400).build();
        }
        List<Assignment> list = assignmentService.getAllAssignments();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/v1/assignments")
    public ResponseEntity<String> createAssignment(@RequestBody String requestStr){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            JsonNode requestJson = JSONValidatorService.validateJSON(requestStr, SCHEMA_PATH);
            log.info("Validated JSON String");
            assignmentService.createAssignment(requestJson);
            log.info("Created Assignment in Database");
            return ResponseEntity.status(201).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400).build();
        }

    }

    @GetMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> getOne(@PathVariable String id){
        Assignment assignment = assignmentService.getOneAssignment(id);

        return ResponseEntity.status(200).body(assignment);
    }

    @DeleteMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> deleteAssignment(@PathVariable String id){
        try {

            return assignmentService.deleteAssignment(id) ?
                    ResponseEntity.status(204).build() : ResponseEntity.status(403).build();
        }
        catch (Exception e){
            return  new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
    @PutMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> updateAssignments(@RequestBody String requestBody,
                                                    @PathVariable String id){
        JsonNode requestJson = JSONValidatorService.validateJSON(requestBody, SCHEMA_PATH);
        log.info("Validated JSON String");
        Assignment assignment = new Assignment();
        assignment.setName(requestJson.get("name").textValue());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(requestJson.get("deadline").textValue(), inputFormatter);
//        String formattedDate = outputFormatter.format(date);
        assignment.setDeadline(date);
        assignment.setPoints(requestJson.get("points").intValue());
        assignment.setAssignmentUpdated(LocalDateTime.now());
        if (!assignmentService.updateAssignment(id, assignment)){
            return ResponseEntity.status(404).build();
        }
        log.info("Updated Assignment in Database");
        return ResponseEntity.status(204).build();

    }
    @PatchMapping("v1/assignments")
    public ResponseEntity<String> patchAssignment(){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("405-Method Not Allowed");
    }
}
