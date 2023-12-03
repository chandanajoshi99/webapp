package com.csye6225.Assignment3.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.csye6225.Assignment3.entity.Submission;
import com.csye6225.Assignment3.response.SubmissionResponse;

import java.util.UUID;

public interface SubmissionServiceImpl {




        SubmissionResponse submitAssignment(UUID id, JsonNode requestBody, int contentLength) throws Exception;

}
