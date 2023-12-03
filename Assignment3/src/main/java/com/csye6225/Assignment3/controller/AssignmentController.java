package com.csye6225.Assignment3.controller;

import com.csye6225.Assignment3.entity.Assignment;
import com.csye6225.Assignment3.response.AssignmentResponse;
import com.csye6225.Assignment3.response.SubmissionResponse;
import com.csye6225.Assignment3.services.AssignmentService;
import com.csye6225.Assignment3.services.JSONValidatorService;
import com.csye6225.Assignment3.services.SubmissionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@Slf4j
public class AssignmentController {
    @Autowired
    private StatsDClient client;
    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
    private static final String SCHEMA_PATH = "static/schema.json";
    private static final String SUBMISSION_SCHEMA_PATH = "static/submission-schema.json";
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private JSONValidatorService JSONValidatorService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SubmissionService submissionService;


    @GetMapping("/healthz")
    public ResponseEntity<Void> databaseConnector(@RequestBody(required = false) String reqStr, @RequestParam(required = false) String reqPara) {
        String path = "/healthz";
        String method = HttpMethod.GET.toString();
        client.increment("api.calls." + method + path);
       try{

           jdbcTemplate.queryForObject("SELECT 9", Integer.class);
                logger.info("Database Connected");
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
//        if (reqStr != null || reqPara !=null){
//            logger.error("Parameters are Given");
//            return ResponseEntity.status(400).build();
//        }
//        String path = "/v1/assignments";
//        String method = HttpMethod.GET.toString();
//        client.increment("api.calls." + method + path);
//
//                try {
//            List<Assignment> list = assignmentService.getAllAssignments();
//            logger.info("HTTP GET request to {} returned status code 200" );
//            return ResponseEntity.ok(list);
//        }
//        catch (Exception e){
//            logger.error("Error Occurred while making GET Request : "+e.getMessage());
//            return ResponseEntity.status(404).build();
//        }
//        List<Assignment> list = assignmentService.getAllAssignments();
//        return ResponseEntity.ok(list);
        String path = "/v1/assignments";
        String method = HttpMethod.GET.toString();
        client.increment("api.calls." + method + path);
        if (reqStr != null || reqPara !=null){
            logger.atError().log("Request Body or Request Parameter is not null");

            return ResponseEntity.status(400).build();
        }
        List<AssignmentResponse> list = assignmentService.getAllAssignments();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/v1/assignments")
    public ResponseEntity<String> createAssignment(@RequestBody String requestStr){
        String path = "/v1/assignments";
        String method = HttpMethod.POST.toString();
        client.increment("api.calls." + method + path);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            JsonNode requestJson = JSONValidatorService.validateJSON(requestStr, SCHEMA_PATH);
            assignmentService.createAssignment(requestJson);
            logger.info("CREATED ASSIGNMENT");
            return ResponseEntity.status(201).build();
        }
        catch (Exception e){
            logger.error(e.getMessage());
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
        String path = "/v1/assignments";
        String method = HttpMethod.DELETE.toString();
        client.increment("api.calls." + method + path);
//        try {
//
//            return assignmentService.deleteAssignment(id) ?
//                    ResponseEntity.status(204).build() : ResponseEntity.status(403).build();
//        }
//        catch (Exception e){
//            return  new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
//        }

        logger.atInfo().log("Deleted Assignment in Database");
        return assignmentService.deleteAssignment(id) ?
                ResponseEntity.status(204).build() : ResponseEntity.status(404).build();
    }
    @PutMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> updateAssignments(@RequestBody String requestBody,
                                                    @PathVariable String id){
        String path = "/v1/assignments";
        String method = HttpMethod.PUT.toString();
        client.increment("api.calls." + method + path);
        JsonNode requestJson = JSONValidatorService.validateJSON(requestBody, SCHEMA_PATH);
        log.debug("Validated JSON String");
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
        String path = "/v1/assignments";
        String method = HttpMethod.PATCH.toString();
        client.increment("api.calls." + method + path);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("405-Method Not Allowed");
    }

    @PostMapping("/v1/assignments/{id}/submission")
    public ResponseEntity<SubmissionResponse> submitAssignment(@RequestBody String requestBody,
                                                               @PathVariable String id, HttpServletRequest request)  {
//        String path = "/v1/assignments";
//        String method = HttpMethod.PUT.toString();
//        client.increment("api.calls." + method + path);
        JsonNode requestJson = JSONValidatorService.validateJSON(requestBody, SUBMISSION_SCHEMA_PATH);
        log.atDebug().log("Validated JSON String" + requestJson);
        String headers = request.getHeader("Content-Length");
        int length = Integer.parseInt(headers);
        SubmissionResponse submission = submissionService.submitAssignment(UUID.fromString(id), requestJson, length);
        log.atDebug().log("Submitted Assignment");
        if (submission == null){
            logger.atError().log("Could Not Submit");
            return ResponseEntity.status(404).build();
        }
        log.info("Validated JSON String");
        return ResponseEntity.status(201).body(submission);

    }
}
