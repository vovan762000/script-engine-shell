package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;


public class ScriptExecutor implements Callable<Script> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Script script;
    private StringWriter sw = new StringWriter();
    private PrintWriter pw = new PrintWriter(sw);
    private ScriptEngine engine;
    private ExecutorService executorService;

    public ScriptExecutor(Script script, ScriptEngine engine) {
        this.engine = engine;
        this.script = script;
        engine.getContext().setWriter(pw);
    }

    @Override
    public Script call() throws ScriptServiceException {
        try {
            script.setStatus("RUNNING");
            Object functionResult = getExecutionResult();
            script.setStatus("FINISHED");
            script.setResult(functionResult);
            if (script.getStatus().equals("FINISHED")) {
                shutdownExecutorService();
            }
            clear();
        } catch (Throwable e) {
            script.setStatus("INTERRUPT " + ", cause: " + e);
            clear();
            throw new ScriptServiceException();
        }
        return script;
    }

    private Object getExecutionResult() throws ScriptException {
        Object functionResult = engine.eval(script.getScript());
        if (functionResult != null) {
            return functionResult;
        }
        return sw.getBuffer();
    }

    private void clear() {
        pw.flush();
        sw.getBuffer().setLength(0);
    }

    protected void shutdownExecutorService() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(5, SECONDS);
        } catch (InterruptedException e) {
            log.error("tasks interrupted");
        } finally {
            if (!executorService.isTerminated()) {
                log.error("cancel non-finished tasks");
            }
            executorService.shutdownNow();
        }
        log.debug("ExecutorService was shutdown - {}", executorService.isShutdown());
    }

    public Script getScript() {
        return script;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
