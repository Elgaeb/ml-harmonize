package com.marklogic.dhf.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.hub.util.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class JobList {

    private Integer total;
    private Integer page;
    private Integer pageSize;
    private List<Job> jobs;

    public JobList deserialize(JsonNode json) {
        JSONObject jsonObject = new JSONObject(json);

        setPage(jsonObject.getInt("page"));
        setPageSize(jsonObject.getInt("pageSize"));
        setTotal(jsonObject.getInt("total"));

        setJobs(jsonObject.getArray("jobs").stream().map(job -> new Job().deserialize((JsonNode) job)).collect(Collectors.toList()));

        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
