package com.gmail.vovan762000.scriptengineshell.controller;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.reader.ScriptReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {
    private final int TEST_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("BlockedScriptReader")
    private ScriptReader blockedScriptReader;

    @MockBean
    @Qualifier("NonBlockedScriptReader")
    private ScriptReader nonBlockedScriptReader;

    @Test
    public void executeScript() throws Exception {
        Script script = new Script();
        script.setScript("test");
        given(this.blockedScriptReader.addAndExecuteScript(any(Script.class))).willReturn(script);
        mockMvc.perform(post("/blocked/scripts")
                .content("test"))
                .andExpect(status().isOk());
        given(this.nonBlockedScriptReader.addAndExecuteScript(any(Script.class))).willReturn(script);
        mockMvc.perform(post("/nonblocked/scripts")
                .content("test"))
                .andExpect(status().isOk());
    }

    @Test
    public void getScriptById() throws Exception {
        Script script = new Script();
        script.setScript("test");
        given(this.blockedScriptReader.getScriptById(TEST_ID)).willReturn(script);
        mockMvc.perform(get("/blocked/scripts/" + TEST_ID))
                .andExpect(status().isOk());
        given(this.nonBlockedScriptReader.getScriptById(TEST_ID)).willReturn(script);
        mockMvc.perform(get("/nonblocked/scripts/" + TEST_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void stopAndRemoveScript() throws Exception {
        Mockito.doNothing().when(blockedScriptReader).deleteScript(TEST_ID);
        mockMvc.perform(delete("/blocked/scripts/" + TEST_ID))
                .andExpect(status().isOk());
        Mockito.doNothing().when(nonBlockedScriptReader).deleteScript(TEST_ID);
        mockMvc.perform(delete("/nonblocked/scripts/" + TEST_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void allScripts() throws Exception {
        Script script1 = new Script();
        script1.setScript("test1");
        Script script2 = new Script();
        script2.setScript("test2");
        List<Script> scripts = new ArrayList<>();
        scripts.add(script1);
        scripts.add(script2);

        given(this.blockedScriptReader.getAllScripts()).willReturn(scripts);
        mockMvc.perform(get("/blocked/scripts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(this.nonBlockedScriptReader.getAllScripts()).willReturn(scripts);
        mockMvc.perform(get("/nonblocked/scripts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}