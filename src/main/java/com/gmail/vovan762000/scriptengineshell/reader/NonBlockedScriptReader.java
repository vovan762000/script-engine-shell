package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.FailedCompilationScriptException;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.util.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component("NonBlockedScriptReader")
public class NonBlockedScriptReader implements ScriptReader, Condition {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private StringWriter sw = new StringWriter();
    private PrintWriter pw = new PrintWriter(sw);

    @Autowired
    private EngineManager engineManager;
    private Map<Script, Thread> threadMap = new ConcurrentHashMap<>();

    @Override
    public List<Script> getAllScripts() {
        return threadMap.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public Script addAndExecuteScript(Script script) throws ScriptServiceException {
        ScriptEngine engine = engineManager.get();
        if (!engineManager.compile(script.getScript(), engine)) {
            throw new FailedCompilationScriptException();
        }
        engine.getContext().setWriter(pw);
        Runnable task = () -> {
            try {
                script.setStatus("RUNNING");
                Object functionResult = engine.eval(script.getScript());
                script.setStatus("FINISHED");
                script.setResult(functionResult);
            } catch (Throwable e) {
                script.setStatus("INTERRUPT" + ", cause: " + e);
                pw.flush();
                sw.getBuffer().setLength(0);
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        threadMap.put(script, thread);
        return script;
    }

    @Override
    public void deleteScript(int scriptId){
        Script currentScript = threadMap.keySet().stream().filter(e -> e.getId()==scriptId).findFirst().get();
        Thread thread = threadMap.get(currentScript);
        while (thread.isAlive()) {
            System.out.println(thread.isAlive());
            thread.stop();
        }
        threadMap.remove(currentScript);
        log.debug("ScriptExecutor with id: '" + scriptId + "' was removed from handler");
    }

    @Override
    public Script getScriptById(int scriptId) {
        return threadMap.keySet().stream().filter(e -> e.getId()==scriptId).findFirst().get();
    }

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return conditionContext.getEnvironment().getProperty("handler").contains("nonblocked");
    }
}
