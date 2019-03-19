package com.gmail.vovan762000.scriptengineshell.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such script")
public class NoSuchScriptException extends ScriptServiceException {
}
