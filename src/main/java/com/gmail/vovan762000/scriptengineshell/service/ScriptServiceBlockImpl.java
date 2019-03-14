package com.gmail.vovan762000.scriptengineshell.service;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.reader.ScriptReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service("ScriptServiceBlockImpl")
public class ScriptServiceBlockImpl implements ScriptService {

    @Autowired
    @Qualifier("BlockedScriptReader")
    private ScriptReader scriptReader;

    @Override
    public Script getById(int id) throws ScriptServiceException {
        return scriptReader.getScriptById(id);
    }

    @Override
    public List<Script> getAll() throws ExecutionException, InterruptedException {
        return scriptReader.getAllScripts();
    }

    @Override
    public Script execute(Script script) throws InterruptedException, ExecutionException, ScriptServiceException {

        return scriptReader.addAndExecuteScript(script);
    }

    @Override
    public void deleteById(int id) throws ScriptServiceException {
        scriptReader.deleteScript(id);
    }
}
