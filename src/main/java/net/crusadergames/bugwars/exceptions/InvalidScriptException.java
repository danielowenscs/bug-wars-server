package net.crusadergames.bugwars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidScriptException extends RuntimeException{
    public InvalidScriptException(String message) {
        super(message);
    }
}
