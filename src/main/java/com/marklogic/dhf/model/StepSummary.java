package com.marklogic.dhf.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.hub.util.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class StepSummary {

    private StepResponse stepResponse;
    private Integer start;
    private Integer pageSize;
    private Integer total;
    private List<Batch> batches;

    public StepSummary deserialize(JsonNode json) {
        JSONObject jsonObject = new JSONObject(json);

        JsonNode stepResponseNode = jsonObject.getNode("stepResponse");
        if(stepResponseNode != null) {
            setStepResponse(new StepResponse(null).deserialize(stepResponseNode));
        }

        setBatches(jsonObject.getArray("batches").stream().map(batch -> {
            JsonNode batchNode = (JsonNode) batch;
            return new Batch().deserialize(batchNode);
        }).collect(Collectors.toList()));

        setStart(jsonObject.getInt("start"));
        setPageSize(jsonObject.getInt("pageSize"));
        setTotal(jsonObject.getInt("total"));

        return this;
    }

    public StepResponse getStepResponse() {
        return stepResponse;
    }

    public void setStepResponse(StepResponse stepResponse) {
        this.stepResponse = stepResponse;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }
}
