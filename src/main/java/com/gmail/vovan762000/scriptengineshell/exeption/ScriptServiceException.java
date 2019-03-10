package com.gmail.vovan762000.scriptengineshell.exeption;

public class ScriptServiceException extends Exception {
    private static final String DEFAULT_MSG = "Attempt to use script service was failed";

    public ScriptServiceException() {
        super(DEFAULT_MSG);
    }

    public ScriptServiceException(String msg) {
        super(msg);
    }
}