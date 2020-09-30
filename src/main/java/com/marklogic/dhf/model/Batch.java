package com.marklogic.dhf.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.dhf.DateUtil;
import com.marklogic.hub.util.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Batch {
    private String jobId;
    private String batchId;
    private String stepNumber;
    private String batchStatus;
    private LocalDateTime timeStarted;
    private LocalDateTime timeEnded;
    private String hostName;
    private LocalDateTime reqTimeStamp;
    private String reqTrnxID;
    private LocalDateTime writeTimeStamp;
    private String writeTrnxID;
    private List<String> uris;
    private String errorStack;
    private String error;
    private CompleteError completeError;
    private Duration batchDuration;

    public Batch deserialize(JsonNode json) {
        JSONObject jsonObject = new JSONObject(json);

        setJobId(jsonObject.getString("jobId"));
        setBatchId(jsonObject.getString("batchId"));
        setStepNumber(jsonObject.getString("stepNumber"));
        setBatchStatus(jsonObject.getString("batchStatus"));
        setTimeStarted(DateUtil.fromISO(jsonObject.getString("timeStarted")));
        setTimeEnded(DateUtil.fromISO(jsonObject.getString("timeEnded")));

        setHostName(jsonObject.getString("hostName"));
        setReqTimeStamp(DateUtil.fromISO(jsonObject.getString("reqTimeStamp")));
        setReqTrnxID(jsonObject.getString("reqTrnxID"));
        setWriteTimeStamp(DateUtil.fromISO(jsonObject.getString("writeTimeStamp")));
        setWriteTrnxID(jsonObject.getString("writeTrnxID"));

        setUris(jsonObject.getArray("uris").stream().map(uri -> uri.toString().replaceAll("^\"|\"$", "")).collect(Collectors.toList()));

        setErrorStack(jsonObject.getString("errorStack"));
        setError(jsonObject.getString("error"));

        JsonNode completeErrorNode = jsonObject.getNode("completeError");
        if(completeErrorNode != null) {
            setCompleteError(new CompleteError().deserialize(completeErrorNode));
        }

        if (getTimeStarted() != null && getTimeEnded() != null) {
            setBatchDuration(Duration.between(this.timeStarted, this.timeEnded));
        }

        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public LocalDateTime getReqTimeStamp() {
        return reqTimeStamp;
    }

    public void setReqTimeStamp(LocalDateTime reqTimeStamp) {
        this.reqTimeStamp = reqTimeStamp;
    }

    public String getReqTrnxID() {
        return reqTrnxID;
    }

    public void setReqTrnxID(String reqTrnxID) {
        this.reqTrnxID = reqTrnxID;
    }

    public LocalDateTime getWriteTimeStamp() {
        return writeTimeStamp;
    }

    public void setWriteTimeStamp(LocalDateTime writeTimeStamp) {
        this.writeTimeStamp = writeTimeStamp;
    }

    public String getWriteTrnxID() {
        return writeTrnxID;
    }

    public void setWriteTrnxID(String writeTrnxID) {
        this.writeTrnxID = writeTrnxID;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public String getErrorStack() {
        return errorStack;
    }

    public void setErrorStack(String errorStack) {
        this.errorStack = errorStack;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Duration getBatchDuration() {
        return batchDuration;
    }

    public void setBatchDuration(Duration batchDuration) {
        this.batchDuration = batchDuration;
    }

    public CompleteError getCompleteError() {
        return completeError;
    }

    public void setCompleteError(CompleteError completeError) {
        this.completeError = completeError;
    }
}
