package com.csye6225.Assignment3.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface JSONValidatorService {
    JsonNode validateJSON(String json, String schemaPath);

}
