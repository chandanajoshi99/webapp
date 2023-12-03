package com.csye6225.Assignment3.services;

import com.csye6225.Assignment3.exceptions.AssignmentNotFoundException;
import com.csye6225.Assignment3.exceptions.CannotAccessException;
import com.csye6225.Assignment3.repository.AssignmentRepository;
import com.csye6225.Assignment3.auth.BasicAuthenticationManager;
import com.csye6225.Assignment3.entity.Assignment;
import com.csye6225.Assignment3.response.AssignmentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;

    @Autowired
    private final BasicAuthenticationManager authenticationManager;
    @Autowired
    private EntityManager entityManager;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, BasicAuthenticationManager authenticationManager) {
        this.assignmentRepository = assignmentRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void createAssignment(JsonNode reqNode) {
        Assignment assignment = new Assignment();
        assignment.setName(reqNode.get("name").textValue());
        assignment.setPoints(reqNode.get("points").intValue());
        assignment.setNumber_of_Attempts(reqNode.get("number_of_attempts").intValue());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(reqNode.get("deadline").textValue(), inputFormatter);
        assignment.setDeadline(date);
        assignment.setAssignmentCreated(LocalDateTime.now());
        assignment.setAssignmentUpdated(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assignment.setOwnerEmail(authentication.getName());
        assignmentRepository.save(assignment);
    }


    @Override
    public Assignment getOneAssignment(String id) {
        if (id == null) {
           // log.warn("INVALID USER");
            throw new IllegalArgumentException("Invalid User");
        }
        Optional<Assignment> optionalUser = Optional.ofNullable(assignmentRepository.findById(id));
        Assignment assignment = optionalUser.orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));
        if (!assignment.getOwnerEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){

            throw new CannotAccessException("Cannot access requested resource");
        }

        return assignmentRepository.findById(id);
    }

    @Override
    public List<AssignmentResponse> getAllAssignments() {
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream().map(this::mapAssignmentToResponse).toList();
    }

    @Override
    @Transactional
    public boolean deleteAssignment(String id) {

//
        Assignment assignment = assignmentRepository.findById(id);

        if (assignment ==  null)
            throw new AssignmentNotFoundException("Assignment not found");
        if (assignment.getOwnerEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
//            Query query = entityManager.createQuery("delete from Assignment a WHERE a.id=:id");
//            query.setParameter("id", uuid);
//            query.executeUpdate();
            assignmentRepository.deleteById(id);
           // log.info("SUCCESSFULLY DELETED");
            return true;
        }
        throw new CannotAccessException("Cannot access the requested Data");
    }
    @Override
    public boolean updateAssignment(String id, Assignment requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignment assignment1 = assignmentRepository
                .findById(id);
        if (assignment1 == null){
            return false;
        }
        if (authentication.getPrincipal().equals(assignment1.getOwnerEmail())){
            assignment1.setName(requestBody.getName());
            assignment1.setPoints(requestBody.getPoints());
            assignment1.setNumber_of_Attempts(requestBody.getNumber_of_Attempts());
            return assignmentRepository.save(assignment1) != null ? true : false;
        }
        else
            return false;
    }

    private AssignmentResponse mapAssignmentToResponse(Assignment assignment){
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setId(UUID.fromString(assignment.getId()));
        assignmentResponse.setName(assignment.getName());
        assignmentResponse.setPoints(assignment.getPoints());
        assignmentResponse.setNum_of_attempts(assignment.getNumber_of_Attempts());
        assignmentResponse.setDeadline(assignment.getDeadline());
        assignmentResponse.setAssignmentCreated(assignment.getAssignmentCreated());
        assignmentResponse.setAssignmentUpdated(assignment.getAssignmentUpdated());
        return assignmentResponse;
    }


}




