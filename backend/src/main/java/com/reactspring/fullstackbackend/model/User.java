package com.reactspring.fullstackbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;


import java.io.Serializable;

@Entity
@Table(name = "USER_ALL", indexes = {@Index(columnList = "id", unique = true)})
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", columnDefinition = "VARCHAR(50)")
    private String id;

    @NotBlank(message = "Username shouldn't be empty")
    @Size(min = 2, max = 100, message = "Username should have at least 2 characters and shouldn't have more than 100 characters")
    @Column(name = "USER_NAME", columnDefinition = "VARCHAR(100)", nullable = false, unique = true)
    private String userName;

    @Size(min = 2, max = 120, message = "Name should have at least 2 characters and shouldn't have more than 120 characters")
    @NotBlank(message = "Name shouldn't be empty")
    @Column(name = "NAME", columnDefinition = "VARCHAR(120)", nullable = false)
    private String name;

    @Email(message = "Email address is not valid")
    @Size(min = 2, max = 120, message = "Email should have at least 2 characters and shouldn't have more than 120 characters")
    @NotBlank(message = "Email shouldn't be empty")
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(120)", nullable = false, unique = true)
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
