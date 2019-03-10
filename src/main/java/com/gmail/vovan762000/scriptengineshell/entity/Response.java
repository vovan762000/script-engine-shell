package com.gmail.vovan762000.scriptengineshell.entity;

import org.springframework.hateoas.ResourceSupport;

public class Response extends ResourceSupport {
    private Object content;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
