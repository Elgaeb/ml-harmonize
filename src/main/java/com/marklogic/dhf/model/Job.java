package com.marklogic.dhf.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.dhf.DateUtil;
import com.marklogic.hub.flow.Flow;
import com.marklogic.hub.util.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Job {

    private String jobId;
    private String flow;
    private String user;
    private String lastAttemptedStep;
    private String lastCompletedStep;
    private String jobStatus;
    private LocalDateTime timeStarted;
    private LocalDateTime timeEnded;
    private Duration jobDuration;
    private List<StepResponse> stepResponses;

    // <editor-fold desc="Getters & Setters">
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLastAttemptedStep() {
        return lastAttemptedStep;
    }

    public void setLastAttemptedStep(String lastAttemptedStep) {
        this.lastAttemptedStep = lastAttemptedStep;
    }

    public String getLastCompletedStep() {
        return lastCompletedStep;
    }

    public void setLastCompletedStep(String lastCompletedStep) {
        this.lastCompletedStep = lastCompletedStep;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(LocalDateTime timeStarted) {
        this.timeStarted = timeStarted;
    }

    public LocalDateTime getTimeEnded() {
        return timeEnded;
    }

    public void setTimeEnded(LocalDateTime timeEnded) {
        this.timeEnded = timeEnded;
    }

    public List<StepResponse> getStepResponses() {
        return stepResponses;
    }

    public void setStepResponses(List<StepResponse> stepResponses) {
        this.stepResponses = stepResponses;
    }

    public Duration getJobDuration() {
        return jobDuration;
    }

    public void setJobDuration(Duration jobDuration) {
        this.jobDuration = jobDuration;
    }
    // </editor-fold>

    public Job deserialize(JsonNode json) {
        JSONObject jsonObject = new JSONObject(json);

        setJobId(jsonObject.getString("jobId"));
        setFlow(jsonObject.getString("flow"));
        setUser(jsonObject.getString("user"));
        setLastAttemptedStep(jsonObject.getString("lastAttemptedStep"));
        setLastCompletedStep(jsonObject.getString("lastCompletedStep"));
        setJobStatus(jsonObject.getString("jobStatus"));
        setTimeStarted(DateUtil.fromISO(jsonObject.getString("timeStarted")));
        setTimeEnded(DateUtil.fromISO(jsonObject.getString("timeEnded")));
        if(getTimeStarted() != null && getTimeEnded() != null) {
            setJobDuration(Duration.between(this.timeStarted, this.timeEnded));
        }

        Map<String, Object> stepResponses = jsonObject.getMap("stepResponses");
        List<StepResponse> responses = stepResponses.keySet().stream().map(key -> {
            JsonNode stepResponseNode = (JsonNode) stepResponses.get(key);
            return new StepResponse(key).deserialize(stepResponseNode);
        }).collect(Collectors.toList());

        responses.sort(Collections.reverseOrder((a, b) -> {
            if(a.getStepStartTime() != null && b.getStepStartTime() != null) {
                return a.getStepStartTime().compareTo(b.getStepStartTime());
            } else if(a.getStepStartTime() == null && a.getStepStartTime() == null) {
                return 0;
            } else if(a.getStepStartTime() == null) {
                return -1;
            } else {
                return 1;
            }
        }));

        setStepResponses(responses);



        return this;
    }
}
