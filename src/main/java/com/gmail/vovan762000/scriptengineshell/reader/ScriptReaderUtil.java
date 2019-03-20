package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.FailedCompilationScriptException;
import com.gmail.vovan762000.scriptengineshell.exeption.NoSuchScriptException;
import com.gmail.vovan762000.scriptengineshell.util.EngineManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ScriptReaderUtil {

    static List<Script> allScripts(Map<Integer, ScriptExecutor> map) {
        return map.values().stream().map(e -> e.getScript()).collect(Collectors.toList());
    }

    static void deleteScript(Map<Integer, ScriptExecutor> map, int scriptId) throws NoSuchScriptException {
        if (map.get(scriptId) == null) {
            throw new NoSuchScriptException();
        }
        map.get(scriptId).shutdownExecutorService();
        map.remove(scriptId);
    }

    static Script getScript(Map<Integer, ScriptExecutor> map, int scriptId) throws NoSuchScriptException {
        if (map.get(scriptId) == null) {
            throw new NoSuchScriptException();
        }
        return map.get(scriptId).getScript();
    }

    static Future<Script> addScript(Map<Integer, ScriptExecutor> map, EngineManager engineManager, Script script) throws FailedCompilationScriptException {
        if (!engineManager.compile(script.getScript(), engineManager.get())) {
            throw new FailedCompilationScriptException();
        }
        ScriptExecutor scriptExecutor = new ScriptExecutor(script, engineManager.get());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        scriptExecutor.setExecutorService(executorService);
        Future<Script> future = executorService.submit(scriptExecutor);
        map.put(script.getId(), scriptExecutor);
        return future;
    }
}
