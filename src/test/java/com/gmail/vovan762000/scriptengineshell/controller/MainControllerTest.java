package com.gmail.vovan762000.scriptengineshell.controller;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.service.ScriptService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ScriptService scriptService;

    @Test
    public void executeScript() throws Exception {
        Script script = new Script();
        script.setScript("test");
        given(this.scriptService.execute(any(Script.class))).willReturn(script);
        mockMvc.perform(post("/execute")
                .content("test"))
                .andExpect(status().isOk());
    }

    @Test
    public void getScriptExecutorById() throws Exception {
        Script script = new Script();
        script.setScript("test");
        given(this.scriptService.getById(TEST_ID)).willReturn(script);

        mockMvc.perform(get("/script/" + TEST_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void stopAndRemoveScript() throws Exception {
        Mockito.doNothing().when(scriptService).deleteById(TEST_ID);
        mockMvc.perform(delete("/delete/" + TEST_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void all() throws Exception {
        Script script1 = new Script();
        script1.setScript("test1");
        Script script2 = new Script();
        script2.setScript("test2");
        List<Script> scripts = new ArrayList<>();
        scripts.add(script1);
        scripts.add(script2);

        given(this.scriptService.getAll()).willReturn(scripts);
        mockMvc.perform(get("/all")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}