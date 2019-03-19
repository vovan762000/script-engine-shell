package com.gmail.vovan762000.scriptengineshell.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Compilation failed")
public class FailedCompilationScriptException extends ScriptServiceException {
}

