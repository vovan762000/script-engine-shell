package com.gmail.vovan762000.scriptengineshell.service;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ScriptService {

    Script getById(int id) throws ScriptServiceException;

    List<Script> getAll() throws ExecutionException, InterruptedException;

    Script execute (Script script) throws InterruptedException, ExecutionException, ScriptServiceException;

    void deleteById(int id) throws ScriptServiceException;

}
