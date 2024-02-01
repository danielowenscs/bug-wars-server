package net.crusadergames.bugwars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Must be an Admin to perform this task.")

public class NotAnAdminException extends Exception{
}
