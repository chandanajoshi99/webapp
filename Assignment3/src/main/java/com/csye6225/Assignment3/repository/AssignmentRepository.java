package com.csye6225.Assignment3.repository;

import com.csye6225.Assignment3.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long> {

    Assignment findById(String id);

    //Assignment findByIdAndOwnerEmail(String id, String ownerEmail);


}
