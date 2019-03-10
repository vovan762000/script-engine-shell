package com.gmail.vovan762000.scriptengineshell.controller;

import com.gmail.vovan762000.scriptengineshell.entity.Response;
import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private int startSeq = 1;

    @Autowired
    private ScriptService scriptService;

    @PostMapping("/execute")
    public ResponseEntity<Response> executeScript(@RequestBody String script) throws Exception {
        Response responseBody = new Response();
        Script newScript = new Script();
        newScript.setId(startSeq++);
        newScript.setScript(script);
        Script scriptObj = scriptService.execute(newScript);
        responseBody.setContent(scriptObj);
        log.info("executed script with id  {}", newScript.getId());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/script/{id}")
    public ResponseEntity<Response> getScriptExecutorById(@PathVariable int id) throws Exception {
        Response responseBody = new Response();
        Script script = scriptService.getById(id);
        responseBody.setContent(script);
        log.info("get script with id  {}", id);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity stopAndRemoveScript(@PathVariable int id) throws Exception {
        scriptService.deleteById(id);
        log.info("delete script with id  {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Response>> all() throws Exception {
        List<Response> responseList = new ArrayList<>();
        List<Script> scripts = scriptService.getAll();
        scripts.forEach(script -> {
            Response responseBody = new Response();
            responseBody.setContent(script);
            responseList.add(responseBody);
        });
        log.info("getAll");
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

}
