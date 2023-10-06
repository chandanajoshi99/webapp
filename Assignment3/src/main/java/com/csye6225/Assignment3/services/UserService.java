package com.csye6225.Assignment3.services;

import com.csye6225.Assignment3.repository.UserRepository;
import com.csye6225.Assignment3.entity.User;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void loadDataFromCsv() {
        System.out.println("Current Working Directory: " + System.getProperty("user.dir"));

        try (CSVReader csvReader = new CSVReader(new FileReader(System.getProperty("user.dir")+"/Assignment3/src/main/resources/static/opt/users.csv"))) {

            String[] line;
            csvReader.readNext();
            while ((line = csvReader.readNext()) != null) {
                String username = line[0];
                String lastName = line[1];
                String email = line[2]; // Assuming the email is in the user column
                String password = line[3];// Password as Password

                // Check if the user already exists
                Optional<User> existingUser = userRepository.findByEmail(email);

                if (existingUser.isEmpty()) {
                    User newUser = new User();
                    newUser.setFirstName(username);
                    newUser.setLastName(lastName);
                    newUser.setEmail(email);
                    newUser.setAccountCreated(LocalDateTime.now());
                    newUser.setAccountUpdated(LocalDateTime.now());
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    newUser.setPassword(passwordEncoder.encode(password));
                    userRepository.save(newUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            thr