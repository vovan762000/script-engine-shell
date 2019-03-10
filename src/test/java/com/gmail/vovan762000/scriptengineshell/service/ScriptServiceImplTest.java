package com.gmail.vovan762000.scriptengineshell.service;

import com.gmail.vovan762000.scriptengineshell.ScriptEngineShellApplication;
import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.gmail.vovan762000.scriptengineshell.ScriptTestData.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScriptEngineShellApplication.class)
public class ScriptServiceImplTest {
    private int startSeq = 1;

    @Autowired
    ScriptService scriptService;

    @Before
    public void before() throws InterruptedException, ExecutionException, ScriptServiceException {
        for (int i = 0; i < 3; i++) {
            Script newScript = new Script();
            newScript.setId(startSeq++);
            newScript.setScript("function m(){\n" +
                    "return 'test';\n" +
                    "}\n" +
                    "m(); ");
            scriptService.execute(newScript);
        }
    }

    @After
    public void after() throws ExecutionException, InterruptedException {
        scriptService.getAll().clear();
    }

    @Test
    public void getById() throws ScriptServiceException {
        Script actualScript = scriptService.getById(1);
        assertMatch(actualScript,SCRIPT_1);
    }

    @Test
    public void getAll() throws ExecutionException, InterruptedException {
        assertMatch(scriptService.getAll(),SCRIPTS);
    }

    @Test
    public void execute() throws InterruptedException, ExecutionException, ScriptServiceException {
        Script newScript = new Script();
        newScript.setId(startSeq++);
        newScript.setScript("function m(){\n" +
                "return 'test';\n" +
                "}\n" +
                "m(); ");
        scriptService.execute(newScript);
        assertMatch(newScript,SCRIPT_4);
    }

    @Test
    public void deleteById() throws ScriptServiceException {
    }
}