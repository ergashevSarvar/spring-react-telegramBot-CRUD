package com.reactspring.fullstackbackend.exception;

public class UserNotFundException extends RuntimeException{
     public UserNotFundException(String id){
         super("Could not found user  by id: " + id);
     }
}
