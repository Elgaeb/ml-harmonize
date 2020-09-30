package com.marklogic.dhf.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.dhf.DateUtil;
import com.marklogic.hub.util.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class StepResponse {

    private String step;
    private String flowName;
    private String stepName;
    private String stepDefinitionName;
    private String stepDefinitionType;
    private String stepOutput;
    private String fullOutput;
    private String status;
    private Integer totalEvents;
    private Integer successfulEvents;
    private Integer failedEvents;
    private Integer successfulBatches;
    private Integer failedBatches;
    private Boolean success;
    private LocalDateTime stepStartTime;
    private LocalDateTime stepEndTime;
    private Duration stepDuration;

    // <editor-fold desc="Getters & Setters">
    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDefinitionName() {
        return stepDefinitionName;
    }

    public void setStepDefinitionName(String stepDefinitionName) {
        this.stepDefinitionName = stepDefinitionName;
    }

    public String getStepDefinitionType() {
        return stepDefinitionType;
    }

    public void setStepDefinitionType(String stepDefinitionType) {
        this.stepDefinitionType = stepDefinitionType;
    }

    public String getStepOutput() {
        return stepOutput;
    }

    public void setStepOutput(String stepOutput) {
        this.stepOutput = stepOutput;
    }

    public String getFullOutput() {
        return fullOutput;
    }

    public void setFullOutput(String fullOutput) {
        this.fullOutput = fullOutput;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Integer totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Integer getSuccessfulEvents() {
        return successfulEvents;
    }

    public void setSuccessfulEvents(Integer successfulEvents) {
        this.successfulEvents = successfulEvents;
    }

    public Integer getFailedEvents() {
        return failedEvents;
    }

    public void setFailedEvents(Integer failedEvents) {
        this.failedEvents = failedEvents;
    }

    public Integer getSuccessfulBatches() {
        return successfulBatches;
    }

    public void setSuccessfulBatches(Integer successfulBatches) {
        this.successfulBatches = successfulBatches;
    }

    public Integer getFailedBatches() {
        return failedBatches;
    }

    public void setFailedBatches(Integer failedBatches) {
        this.failedBatches = failedBatches;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getStepStartTime() {
        return stepStartTime;
    }

    public void setStepStartTime(LocalDateTime stepStartTime) {
        this.stepStartTime = stepStartTime;
    }

    public LocalDateTime getStepEndTime() {
        return stepEndTime;
    }

    public void setStepEndTime(LocalDateTime stepEndTime) {
        this.stepEndTime = stepEndTime;
    }

    public Duration getStepDuration() {
        return stepDuration;
    }

    public void setStepDuration(Duration stepDuration) {
        this.stepDuration = stepDuration;
    }
    // </editor-fold>

    public StepResponse(String step) {
        this.step = step;
    }

    public StepResponse deserialize(JsonNode json) {
        JSONObject jsonObject = new JSONObject(json);

        setFlowName(jsonObject.getString("flowName"));
        setStepName(jsonObject.getString("stepName"));
        setStepDefinitionName(jsonObject.getString("stepDefinitionName"));
        setStepDefinitionType(jsonObject.getString("stepDefinitionType"));
        setStepOutput(jsonObject.getString("stepOutput"));
        setFullOutput(jsonObject.getString("fullOutput"));
        setStatus(jsonObject.getString("status"));
        setTotalEvents(jsonObject.getInt("totalEvents"));
        setSuccessfulEvents(jsonObject.getInt("successfulEvents"));
        setFailedEvents(jsonObject.getInt("failedEvents"));
        setSuccessfulBatches(jsonObject.getInt("successfulBatches"));
        setFailedBatches(jsonObject.getInt("failedBatches"));
        setSuccess(jsonObject.getBoolean("success"));
        setStepStartTime(DateUtil.fromISO(jsonObject.getString("stepStartTime")));
        setStepEndTime(DateUtil.fromISO(jsonObject.getString("stepEndTime")));

        if(getStepStartTime() != null && getStepEndTime() != null) {
            setStepDuration(Duration.between(this.stepStartTime, this.stepEndTime));
        }

        return this;
    }

}
