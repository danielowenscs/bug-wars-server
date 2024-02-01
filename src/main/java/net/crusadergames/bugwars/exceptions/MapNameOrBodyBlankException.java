package net.crusadergames.bugwars.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Map name or body is blank.")
public class MapNameOrBodyBlankException extends Exception {
}
