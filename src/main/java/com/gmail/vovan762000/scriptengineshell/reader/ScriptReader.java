package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface ScriptReader {

    List<Script> getAllScripts() throws ExecutionException, InterruptedException;

    Script addAndExecuteScript(Script script) throws ScriptServiceException, ExecutionException, InterruptedException;

    void deleteScript(int scriptId) throws ScriptServiceException;

    Script getScriptById(int scriptId) throws ScriptServiceException;
}
