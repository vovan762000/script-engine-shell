package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.util.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component("BlockedScriptReader")
public class BlockedScriptReader implements ScriptReader {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Map<Integer, ScriptExecutor> blockScExMap = new ConcurrentHashMap<>();

    @Autowired
    private EngineManager engineManager;

    @Override
    public List<Script> getAllScripts() throws ScriptServiceException {
        log.debug("Get all scripts");
        return ScriptReaderUtil.allScripts(blockScExMap);
    }

    public Script addAndExecuteScript(Script script) throws ScriptServiceException, ExecutionException, InterruptedException {
        log.debug("start executing script  " + script.getId());
        return ScriptReaderUtil.addScript(blockScExMap, engineManager, script).get();
    }

    @Override
    public void deleteScriptById(int scriptId) throws ScriptServiceException {
        ScriptReaderUtil.deleteScript(blockScExMap, scriptId);
        log.debug("Script with id: '" + scriptId + "' was removed");
    }

    @Override
    public Script getScriptById(int scriptId) throws ScriptServiceException {
        log.debug("get script with id  {}", scriptId);
        return ScriptReaderUtil.getScript(blockScExMap, scriptId);
    }
}
