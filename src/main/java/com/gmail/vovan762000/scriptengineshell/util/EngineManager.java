package com.gmail.vovan762000.scriptengineshell.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Component
@PropertySource("classpath:config.properties")
public class EngineManager {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${engine.name}")
    private String engineName;

    public ScriptEngine get() {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName(engineName);
        log.debug("New engine was created");
        return engine;
    }

    public boolean compile(String script, ScriptEngine engine) {
        try {
            ((Compilable) engine).compile(script);
            log.debug("Script compiled successful: \n" + script);
            return true;
        } catch (ScriptException e) {
            log.warn("Script \"" + script + "\" didn't compile");
            return false;
        }
    }
}
