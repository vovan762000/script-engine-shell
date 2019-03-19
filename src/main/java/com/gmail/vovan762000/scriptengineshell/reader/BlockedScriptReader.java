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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component("BlockedScriptReader")
public class BlockedScriptReader implements ScriptReader {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Map<Integer, ScriptExecutor> scriptExecutorMap = new ConcurrentHashMap<>();

    @Autowired
    private EngineManager engineManager;

    @Override
    public List<Script> getAllScripts() throws ScriptServiceException {
        List<Script> scripts = new ArrayList<>();
        for (Map.Entry<Integer, ScriptExecutor> scriptExecutorEntry : scriptExecutorMap.entrySet()) {
            scripts.add(scriptExecutorEntry.getValue().getScript());
        }
        return scripts;
    }

    public Script addAndExecuteScript(Script script) throws ScriptServiceException, ExecutionException, InterruptedException {
        if (!engineManager.compile(script.getScript(), engineManager.get())) {
            throw new FailedCompilationScriptException();
        }
        ScriptExecutor scriptExecutor = new ScriptExecutor(script, engineManager.get());
        scriptExecutor.setExecutorService(executorService);
        Future<Script> future = executorService.submit(scriptExecutor);
        scriptExecutorMap.put(script.getId(), scriptExecutor);
        return future.get();
    }

    @Override
    public void deleteScript(int scriptId) throws ScriptServiceException {
        if (scriptExecutorMap.get(scriptId) == null) {
            throw new NoSuchScriptException();
        }
        scriptExecutorMap.get(scriptId).shutdownExecutorService();
        scriptExecutorMap.remove(scriptId);
        log.debug("Script with id: '" + scriptId + "' was removed");
    }

    @Override
    public Script getScriptById(int scriptId) throws ScriptServiceException {
        if (scriptExecutorMap.get(scriptId) == null) {
            throw new NoSuchScriptException();
        }
        return scriptExecutorMap.get(scriptId).getScript();
    }
}
