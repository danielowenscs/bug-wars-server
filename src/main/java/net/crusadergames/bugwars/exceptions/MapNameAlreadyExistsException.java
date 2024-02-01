package net.crusadergames.bugwars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Map name already exists")
public class MapNameAlreadyExistsException extends RuntimeException{

}

