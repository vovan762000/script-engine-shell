package com.gmail.vovan762000.scriptengineshell.entity;

import java.util.Objects;

public class Script {
    private int id;
    private String script;
    private String status;
    private String result;

    public Script() {
    }

    public Script(int id, String script, String status, String result) {
        this.id = id;
        this.script = script;
        this.status = status;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Script script1 = (Script) o;
        return id == script1.id &&
                Objects.equals(script, script1.script) &&
                Objects.equals(status, script1.status) &&
                Objects.equals(result, script1.result);
    }

    @Override
    public int hashCode() {
        int hash = 37;
        hash = hash * 17 + script.hashCode();
        hash = hash * 17 + status.hashCode();
        hash = hash * 17 + result.hashCode();
        return hash;
    }
}
