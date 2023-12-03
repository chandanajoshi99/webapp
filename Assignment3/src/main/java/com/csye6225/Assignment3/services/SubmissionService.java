package com.csye6225.Assignment3.services;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.csye6225.Assignment3.entity.Assignment;
import com.csye6225.Assignment3.entity.SNSMessage;
import com.csye6225.Assignment3.entity.Submission;
import com.csye6225.Assignment3.repository.AssignmentRepository;
import com.csye6225.Assignment3.repository.SubmissionRepository;
import com.csye6225.Assignment3.response.SubmissionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import com.csye6225.Assignment3.exceptions.AssignmentNotFoundException;
import com.csye6225.Assignment3.exceptions.CannotSubmitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@Slf4j
public class SubmissionService implements SubmissionServiceImpl{

    @Value("${SUBMISSION_TOPIC_ARN}")
    private String TOPIC;
    private final AssignmentRepository assignmentRepository;

    private final SubmissionRepository submissionRepository;

    public SubmissionService(AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;

    }

    @Override
    public SubmissionResponse submitAssignment(UUID id, JsonNode requestBody, int contentLength) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignment assignment1 = assignmentRepository
                .findById(String.valueOf(id));
        if (assignment1 == null) {
            throw new AssignmentNotFoundException("Not Found");
        }
      
        if (assignment1.getNumber_of_Attempts() < assignment1.getSubmissions().stream().filter(submission -> submission.getAccountEmail().equals(authentication.getName())).count()){
            throw new CannotSubmitException("No more attempts left");
        }
        if (assignment1.getDeadline().isBefore(LocalDateTime.now())){
            throw new CannotSubmitException("Deadline has passed");
        }
        assignment1.setAssignmentUpdated(LocalDateTime.now());
        assignmentRepository.save(assignment1);
        Submission submission = new Submission();
        submission.setAssignment(assignment1);
        submission.setSubmissionLink(requestBody.get("submission_url").textValue());
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setSubmissionUpdated(LocalDateTime.now());
        submission.setAccountEmail(authentication.getName());
        submission = submissionRepository.save(submission);
        log.atDebug().log("Submission: {}", submission);
        boolean status = false;
        if (contentLength < 1){
            status = false;
        }
        else{
            status = true;
        }

        Regions regions = Regions.US_EAST_1;

        val snsClient = AmazonSNSClientBuilder.defaultClient();

        log.atDebug().log("SNS Client: {}", snsClient);
        SNSMessage snsMessage = new SNSMessage();
        snsMessage.setSubmissionUrl(submission.getSubmissionLink());
        snsMessage.setStatus(status ? "SUCCESS" : "FAILURE");
        snsMessage.setUserEmail(authentication.getName());
        snsMessage.setAssignmentId(String.valueOf(assignment1.getId()));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(snsMessage);
            log.atInfo().log("JSON: {}", json);
        } catch (JsonProcessingException e) {
            log.atError().log("Error: {}", e);
            log.atDebug().log("Error: {}", e);
            throw new RuntimeException(e);
        }
        PublishRequest publishRequest = new PublishRequest(TOPIC, json);


        snsClient.publish(publishRequest);

        log.atInfo().log("SUBMITTED ASSIGNMENT");
        log.atInfo().log("SENT NOTIFICATION");

        log.info("UPDATED SUCCESSFULLY");
        return mapSubmissionToResponse(submission);
    }

    SubmissionResponse mapSubmissionToResponse(Submission submission){
        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setId(submission.getId());
        submissionResponse.setSubmissionLink(submission.getSubmissionLink());
        submissionResponse.setSubmissionDate(submission.getSubmissionDate());
        submissionResponse.setSubmissionUpdated(submission.getSubmissionUpdated());
        submissionResponse.setAssignmentId(UUID.fromString(submission.getAssignment().getId()));
        return submissionResponse;
    }
}
