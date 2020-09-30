package com.marklogic.dhf.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.dhf.DateUtil;
import com.marklogic.hub.util.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CompleteError {
    private String stack;
    private String message;
    private String name;

    public CompleteError deserialize(JsonNode json) {
        JSONObject jsonObject = new JSONObject(json);

        setStack(jsonObject.getString("stack"));
        setMessage(jsonObject.getString("message"));
        setName(jsonObject.getString("name"));

        return this;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
