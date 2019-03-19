package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.FailedCompilationScriptException;
import com.gmail.vovan762000.scriptengineshell.exeption.NoSuchScriptException;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.util.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component("NonBlockedScriptReader")
public class NonBlockedScriptReader implements ScriptReader {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ExecutorService executorService;

    @Autowired
    private EngineManager engineManager;
    private Map<Integer, ScriptExecutor> scriptExecutorMap = new ConcurrentHashMap<>();

    @Override
    public List<Script> getAllScripts() {
        log.debug("Get all scripts");
        return scriptExecutorMap.values().stream().map(e -> e.getScript()).collect(Collectors.toList());
    }

    @Override
    public Script addAndExecuteScript(Script script) throws ScriptServiceException {
        if (!engineManager.compile(script.getScript(), engineManager.get())) {
            throw new FailedCompilationScriptException();
        }
        ScriptExecutor scriptExecutor = new ScriptExecutor(script, engineManager.get());
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(scriptExecutor);
        scriptExecutor.setExecutorService(executorService);
        scriptExecutorMap.put(script.getId(), scriptExecutor);
        log.debug("start executing script  " + script.getId());
        return script;
    }

    @Override
    public void deleteScript(int scriptId) throws ScriptServiceException {
        if (scriptExecutorMap.get(scriptId) == null) {
            throw new NoSuchScriptException();
        }
        ScriptExecutor scriptExecutor = scriptExecutorMap.get(scriptId);
        scriptExecutor.shutdownExecutorService();
        scriptExecutorMap.remove(scriptId);
        log.debug("ScriptExecutor with id: '" + scriptId + "' was removed from handler");
    }

    @Override
    public Script getScriptById(int scriptId) throws ScriptServiceException {
        if (scriptExecutorMap.get(scriptId) == null) {
            throw new NoSuchScriptException();
        }
        log.debug("get script with id  {}", scriptId);
        return scriptExecutorMap.get(scriptId).getScript();
    }
}
