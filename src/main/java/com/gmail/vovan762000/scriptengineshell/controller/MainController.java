package com.gmail.vovan762000.scriptengineshell.controller;

import com.gmail.vovan762000.scriptengineshell.entity.Response;
import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("ScriptServiceBlockImpl")
    private ScriptService blockScriptService;

    @Autowired
    @Qualifier("ScriptServiceNonBlockImpl")
    private ScriptService nonBlockScriptService;

    @PostMapping("/execute/{way}")
    public ResponseEntity<Response> executeScript(@RequestBody String script,@PathVariable String way) throws Exception {
        Response responseBody = new Response();
        Script newScript = new Script();
        newScript.setId(startSeq++);
        newScript.setScript(script);
        Script scriptObj = wayOfRead(way).execute(newScript);
        responseBody.setContent(scriptObj);
        log.info("executed script with id  {}", newScript.getId());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/script/{way}/{id}")
    public ResponseEntity<Response> getScriptById(@PathVariable String way, @PathVariable int id) throws Exception {
        Response responseBody = new Response();
        Script script = wayOfRead(way).getById(id);
        responseBody.setContent(script);
        log.info("get script with id  {}", id);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{way}/{id}")
    public ResponseEntity stopAndRemoveScript(@PathVariable String way,@PathVariable int id) throws Exception {
        wayOfRead(way).deleteById(id);
        log.info("delete script with id  {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/all/{way}")
    public ResponseEntity<List<Response>> allScripts(@PathVariable String way) throws Exception {
        List<Response> responseList = new ArrayList<>();
        List<Script> scripts = wayOfRead(way).getAll();
        scripts.forEach(script -> {
            Response responseBody = new Response();
            responseBody.setContent(script);
            responseList.add(responseBody);
        });
        log.info("getAll");
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    public ScriptService wayOfRead(String way){
        if (way.equals("block")){
            return blockScriptService;
        }else if (way.equals("nonblock")) {
            return nonBlockScriptService;
        }
        return blockScriptService;
    }

}
