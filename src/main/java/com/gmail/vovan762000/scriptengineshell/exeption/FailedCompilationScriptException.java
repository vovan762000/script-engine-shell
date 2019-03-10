package com.gmail.vovan762000.scriptengineshell.exeption;

public class FailedCompilationScriptException extends ScriptServiceException {
    private static final String DEFAULT_MSG = "Compilation failed";

    public FailedCompilationScriptException() {
        super(DEFAULT_MSG);
    }

    public FailedCompilationScriptException(String msg) {
        super(msg);
    }
}

