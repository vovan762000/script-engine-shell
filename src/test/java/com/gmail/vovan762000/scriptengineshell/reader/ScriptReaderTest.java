package com.gmail.vovan762000.scriptengineshell.reader;

import com.gmail.vovan762000.scriptengineshell.ScriptEngineShellApplication;
import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.gmail.vovan762000.scriptengineshell.ScriptTestData.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScriptEngineShellApplication.class)
public class ScriptReaderTest {
    private int startSeq;

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
            scriptReader.addAndExecuteScript(newScript);
        }
    }

    @After
    public void after() throws NoSuchFieldException, IllegalAccessException {
        if (scriptReader instanceof BlockedScriptReader) {
            Field field = scriptReader.getClass().getDeclaredField("blockScExMap");
            field.setAccessible(true);
            Map<Integer, ScriptExecutor> scriptExecutorMap = null;
            scriptExecutorMap = (Map<Integer, ScriptExecutor>) field.get(scriptReader);
            scriptExecutorMap.clear();
        }else{
            Field field = scriptReader.getClass().getDeclaredField("nonBlockScExMap");
            field.setAccessible(true);
            Map<Integer, ScriptExecutor> scriptExecutorMap = null;
            scriptExecutorMap = (Map<Integer, ScriptExecutor>) field.get(scriptReader);
            scriptExecutorMap.clear();
        }
    }

    @Test
    public void getById() throws ScriptServiceException, ExecutionException, InterruptedException, TimeoutException {
        Script actualScript = scriptReader.getScriptById(1);
        assertMatch(actualScript, SCRIPT_1);
    }

    @Test
    public void getAll() throws ScriptServiceException {
        assertMatch(scriptReader.getAllScripts(), SCRIPTS);
    }

    @Test
    public void execute() throws InterruptedException, ExecutionException, ScriptServiceException {
        Script newScript = new Script();
        newScript.setId(startSeq++);
        newScript.setScript("function m(){\n" +
                "return 'test';\n" +
                "}\n" +
                "m(); ");
        scriptReader.addAndExecuteScript(newScript);
        assertMatch(newScript, SCRIPT_4);
    }

    @Test
    public void deleteById() throws ScriptServiceException {
        scriptReader.deleteScriptById(3);
        assertMatch(scriptReader.getAllScripts(), SCRIPT_2, SCRIPT_1);
    }
}