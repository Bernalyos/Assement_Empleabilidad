package com.codeup.domain.exception;

public class UnauthorizedActionException extends DomainException {
    public UnauthorizedActionException() {
        super("You are not allowed to perform this action");
    }

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
