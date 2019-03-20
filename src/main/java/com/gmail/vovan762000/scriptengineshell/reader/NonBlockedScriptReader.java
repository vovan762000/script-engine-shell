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

@Component("NonBlockedScriptReader")
public class NonBlockedScriptReader implements ScriptReader {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EngineManager engineManager;
    private Map<Integer, ScriptExecutor> nonBlockScExMap = new ConcurrentHashMap<>();

    @Override
    public List<Script> getAllScripts() {
        log.debug("Get all scripts");
        return ScriptReaderUtil.allScripts(nonBlockScExMap);
    }

    @Override
    public Script addAndExecuteScript(Script script) throws ScriptServiceException {
        ScriptReaderUtil.addScript(nonBlockScExMap, engineManager, script);
        log.debug("start executing script  " + script.getId());
        return script;
    }

    @Override
    public void deleteScriptById(int scriptId) throws ScriptServiceException {
        ScriptReaderUtil.deleteScript(nonBlockScExMap, scriptId);
        log.debug("ScriptExecutor with id: '" + scriptId + "' was removed from nonBlockScExMap");
    }

    @Override
    public Script getScriptById(int scriptId) throws ScriptServiceException {
        log.debug("get script with id  {}", scriptId);
        return ScriptReaderUtil.getScript(nonBlockScExMap, scriptId);
    }
}
