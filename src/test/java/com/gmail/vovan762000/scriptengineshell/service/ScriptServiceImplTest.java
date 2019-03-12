package com.gmail.vovan762000.scriptengineshell.service;

import com.gmail.vovan762000.scriptengineshell.ScriptEngineShellApplication;
import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.reader.ScriptReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.gmail.vovan762000.scriptengineshell.ScriptTestData.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScriptEngineShellApplication.class)
public class ScriptServiceImplTest {
    private int startSeq;

    @Autowired
    private ScriptService scriptService;

    @Resource(name = "${reader}")
    private ScriptReader scriptReader;

    @Before
    public void before() throws InterruptedException, ExecutionException, ScriptServiceException {
        startSeq = 1;
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
    public void after() throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        if(scriptReader instanceof com.gmail.vovan762000.scriptengineshell.reader.BlockedScriptReader) {
            Field field = scriptReader.getClass().getDeclaredField("futureTaskMap");
            field.setAccessible(true);
            Map<Script, FutureTask<Script>> futureTaskMap = null;
            futureTaskMap = (Map<Script, FutureTask<Script>>) field.get(scriptReader);
            futureTaskMap.clear();
        }else if (scriptReader instanceof com.gmail.vovan762000.scriptengineshell.reader.NonBlockedScriptReader){
            Field field = scriptReader.getClass().getDeclaredField("threadMap");
            field.setAccessible(true);
            Map<Script, Thread> threadMap = null;
            threadMap = (Map<Script, Thread>) field.get(scriptReader);
            threadMap.clear();
        }
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
    public void deleteById() throws ScriptServiceException, ExecutionException, InterruptedException {
        scriptService.deleteById(3);
        assertMatch(scriptService.getAll(),SCRIPT_2,SCRIPT_1);
    }
}