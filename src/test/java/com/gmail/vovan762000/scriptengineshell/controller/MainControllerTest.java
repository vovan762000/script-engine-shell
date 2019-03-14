package com.gmail.vovan762000.scriptengineshell.controller;

import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.service.ScriptService;
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

import javax.annotation.Resource;
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
    @Qualifier("ScriptServiceBlockImpl")
    private ScriptService blockScriptService;

    @MockBean
    @Qualifier("ScriptServiceNonBlockImpl")
    private ScriptService nonBlockScriptService;

    @Test
    public void executeScript() throws Exception {
        Script script = new Script();
        script.setScript("test");
        given(this.blockScriptService.execute(any(Script.class))).willReturn(script);
        mockMvc.perform(post("/execute/block")
                .content("test"))
                .andExpect(status().isOk());
        given(this.nonBlockScriptService.execute(any(Script.class))).willReturn(script);
        mockMvc.perform(post("/execute/nonblock")
                .content("test"))
                .andExpect(status().isOk());
    }

    @Test
    public void getScriptById() throws Exception {
        Script script = new Script();
        script.setScript("test");
        given(this.blockScriptService.getById(TEST_ID)).willReturn(script);
        mockMvc.perform(get("/script/block/" + TEST_ID))
                .andExpect(status().isOk());
        given(this.nonBlockScriptService.getById(TEST_ID)).willReturn(script);
        mockMvc.perform(get("/script/nonblock/" + TEST_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void stopAndRemoveScript() throws Exception {
        Mockito.doNothing().when(blockScriptService).deleteById(TEST_ID);
        mockMvc.perform(delete("/delete/block/" + TEST_ID))
                .andExpect(status().isOk());
        Mockito.doNothing().when(nonBlockScriptService).deleteById(TEST_ID);
        mockMvc.perform(delete("/delete/nonblock/" + TEST_ID))
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

        given(this.blockScriptService.getAll()).willReturn(scripts);
        mockMvc.perform(get("/all/block")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(this.nonBlockScriptService.getAll()).willReturn(scripts);
        mockMvc.perform(get("/all/nonblock")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}