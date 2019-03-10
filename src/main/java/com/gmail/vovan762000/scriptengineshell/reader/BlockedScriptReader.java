package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.FailedCompilationScriptException;
import com.gmail.vovan762000.scriptengineshell.exeption.NoSuchScriptException;
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
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component("BlockedScriptReader")
public class BlockedScriptReader implements ScriptReader, Condition {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private StringWriter sw = new StringWriter();
    private PrintWriter pw = new PrintWriter(sw);
    private ExecutorService executorService = Executors.newWorkStealingPool();
    private Map<Script, FutureTask<Script>> futureTaskMap = new ConcurrentHashMap<>();

    @Autowired
    private EngineManager engineManager;

    @Override
    public List<Script> getAllScripts() {
        return futureTaskMap.keySet().stream().collect(Collectors.toList());
    }

    public Script addAndExecuteScript(Script script) throws ScriptServiceException, ExecutionException, InterruptedException {
        ScriptEngine engine = engineManager.get();
        if (!engineManager.compile(script.getScript(), engine)) {
            throw new FailedCompilationScriptException();
        }
        engine.getContext().setWriter(pw);
        Callable<Script> task = () -> {
            try {
                script.setStatus("RUNNING");
                Object functionResult = engine.eval(script.getScript());
                script.setStatus("FINISHED");
                script.setResult(functionResult);
            } catch (Throwable e) {
                script.setStatus("INTERRUPT" + ", cause: " + e);
                pw.flush();
                sw.getBuffer().setLength(0);
                throw new ScriptServiceException(e.toString());
            }
            return script;
        };
        FutureTask<Script> future = new FutureTask<>(task);
        executorService.execute(future);
        futureTaskMap.put(script, future);
        return future.get();
    }

    @Override
    public void deleteScript(int scriptId) throws ScriptServiceException {
        Script currentScript = futureTaskMap.keySet().stream().filter(e -> e.getId()==scriptId).findFirst().get();
        FutureTask<Script> futureTask = futureTaskMap.get(currentScript);
        if (futureTask == null) {
            throw new NoSuchScriptException();
        }
        while (futureTask.isCancelled()) {
            futureTask.cancel(false);
        }
        futureTaskMap.remove(currentScript);
        log.debug("Script with id: '" + scriptId + "' was removed");
    }

    @Override
    public Script getScriptById(int scriptId) {
        return futureTaskMap.keySet().stream().filter(e -> e.getId()==scriptId).findFirst().get();
    }

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return conditionContext.getEnvironment().getProperty("handler").contains("blocked");
    }
}
