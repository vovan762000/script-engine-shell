package com.gmail.vovan762000.scriptengineshell.service;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.reader.ScriptReader;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Resource(name = "${handler}")
    ScriptReader scriptReader;

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
