package com.khanh.timekeeping.exceptions;


public class ExpiredTokenException extends Exception{
    public ExpiredTokenException(String message) {
        super(message);
    }
}