package com.gmail.vovan762000.scriptengineshell.controller;

import com.gmail.vovan762000.scriptengineshell.entity.Response;
import com.gmail.vovan762000.scriptengineshell.entity.Script;
import com.gmail.vovan762000.scriptengineshell.exeption.ScriptServiceException;
import com.gmail.vovan762000.scriptengineshell.reader.ScriptReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private int startSeq = 1;

    @Autowired
    @Qualifier("BlockedScriptReader")
    private ScriptReader blockedScriptReader;

    @Autowired
    @Qualifier("NonBlockedScriptReader")
    private ScriptReader nonBlockedScriptReader;

    @PostMapping("/{way}/scripts")
    public ResponseEntity<Response> executeScript(@RequestBody String script, @PathVariable String way) throws ScriptServiceException, InterruptedException, ExecutionException, TimeoutException {
        Response responseBody = new Response();
        Script newScript = new Script();
        newScript.setId(startSeq++);
        newScript.setScript(script);
        Script scriptObj = wayOfRead(way).addAndExecuteScript(newScript);
        responseBody.setContent(scriptObj);
        setLinks(responseBody, way, scriptObj);
        log.info("executed script with id  {}", newScript.getId());
        return way.equals("blocked") ? new ResponseEntity<>(responseBody, HttpStatus.OK) : new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{way}/scripts/{id}")
    public ResponseEntity<Response> getScriptById(@PathVariable String way, @PathVariable int id) throws ScriptServiceException, InterruptedException, ExecutionException, TimeoutException {
        Response responseBody = new Response();
        Script script = wayOfRead(way).getScriptById(id);
        responseBody.setContent(script);
        setLinks(responseBody, way, script);
        log.info("get script with id  {}", id);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping("/{way}/scripts/{id}")
    public ResponseEntity stopAndRemoveScript(@PathVariable String way, @PathVariable int id) throws ScriptServiceException {
        wayOfRead(way).deleteScriptById(id);
        log.info("delete script with id  {}", id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{way}/scripts")
    public ResponseEntity<List<Response>> allScripts(@PathVariable String way) throws ScriptServiceException, InterruptedException, ExecutionException, TimeoutException {
        List<Response> responseList = new ArrayList<>();
        List<Script> scripts = wayOfRead(way).getAllScripts();
        for (Script script : scripts) {
            Response responseBody = new Response();
            responseBody.setContent(script);
            responseList.add(responseBody);
            setLinks(responseBody, way, script);
        }
        log.info("getAll");
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    public ScriptReader wayOfRead(String way) {
        return way.equals("blocked") ? blockedScriptReader : nonBlockedScriptReader;
    }

    private void setLinks(Response response, String way, Script script) throws ScriptServiceException, InterruptedException, ExecutionException, TimeoutException {
        response.add(linkTo(methodOn(MainController.class).getScriptById(way, script.getId())).withRel("getScriptById"));
        response.add(linkTo(methodOn(MainController.class).stopAndRemoveScript(way, script.getId())).withRel("stopAndRemoveScript"));
    }

}
