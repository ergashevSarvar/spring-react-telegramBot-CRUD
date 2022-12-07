package com.reactspring.fullstackbackend.controller;

import com.reactspring.fullstackbackend.exception.UserNotFundException;
import com.reactspring.fullstackbackend.model.User;
import com.reactspring.fullstackbackend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<?> newUser(@RequestBody @Valid User user, BindingResult br){
        if(br.hasErrors()) {
            List<FieldError> errors = br.getFieldErrors();
            List<String> message = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();
            for (FieldError e : errors){
                map.put(e.getField(), e.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(map);
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/users")
    List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        if(!userRepository.existsById(id)){
            return ResponseEntity.badRequest().body("This user not found");
        }
        Optional<User> list = userRepository.findById(id);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        if(!userRepository.existsById(id)){
            return ResponseEntity.badRequest().body("This user not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User Deleted succesfully");
    }

    @PutMapping("/user/{id}")
    User updateUser(@RequestBody User newUser, @PathVariable String id){
        return userRepository.findById(id).map(user -> {
            user.setUserName(newUser.getUserName());
            user.setEmail(newUser.getEmail());
            user.setName(newUser.getName());
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFundException(id));
    }

}
