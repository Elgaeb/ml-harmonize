package com.marklogic.dhf.service;

import com.marklogic.hub.HubConfig;
import com.marklogic.hub.flow.FlowInputs;
import com.marklogic.hub.flow.FlowRunner;
import com.marklogic.hub.flow.RunFlowResponse;
import com.marklogic.hub.flow.impl.FlowRunnerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowService {

    private final Logger logger = LoggerFactory.getLogger(FlowService.class);

    private final HubConfigService hubConfigFactory;

    public FlowService(
            @Autowired HubConfigService hubConfigFactory,
            @Value("${marklogic.host}") String host
    ) {
        this.hubConfigFactory = hubConfigFactory;
    }

    public RunFlowResponse runFlow(
            String flowName,
            List<String> steps,
            String jobId,
            String username,
            String password
    ) {
        RunFlowResponse response = null;
        if (flowName != null) {
            HubConfig hubConfig = hubConfigFactory.hubConfig(username, password);
            FlowRunner flowRunner = new FlowRunnerImpl(hubConfig);
            FlowInputs inputs = new FlowInputs(flowName);
            if (steps != null) {
                inputs.setSteps(steps);
            }
            inputs.setJobId(jobId);
            response = flowRunner.runFlow(inputs);
        } else {
            logger.warn("runFlow missing parameters, flowName: " + flowName + "steps: " + steps);
        }
        return response;
    }
}