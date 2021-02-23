package com.marklogic.dhf.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.hub.HubConfig;
import com.marklogic.hub.flow.FlowInputs;
import com.marklogic.hub.flow.FlowRunner;
import com.marklogic.hub.flow.RunFlowResponse;
import com.marklogic.hub.flow.impl.FlowRunnerImpl;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * Service that wraps the Data Hub functionality
 */

@Service
public class DataHubService {

    private final Logger logger = LoggerFactory.getLogger(DataHubService.class);

    private final HubConfigService hubConfigFactory;
    private final String flowNamesJs;

    public DataHubService(
            @Autowired HubConfigService hubConfigFactory,
            @Autowired ResourceLoader resourceLoader,
            @Value("${marklogic.host}") String host
    ) throws IOException {
        this.hubConfigFactory = hubConfigFactory;

        try (InputStream is = resourceLoader.getResource("classpath:/javascript/flowNames.sjs").getInputStream()) {
            this.flowNamesJs = StreamUtils.copyToString(is, Charsets.UTF_8);
        }
    }

//    @Cacheable("flows")
    public List<String> allFlows(
            String username,
            String password
    ) {
        HubConfig hubConfig = hubConfigFactory.hubConfig(username, password);
        DatabaseClient stagingClient = hubConfig.newStagingClient();

        ArrayList<String> flowNames = new ArrayList<>();
        JsonNode rootNode = stagingClient.newServerEval().javascript(this.flowNamesJs).evalAs(JsonNode.class);
        if(rootNode.isArray()) {
            for(JsonNode jsonNode : rootNode) {
                flowNames.add(jsonNode.asText());
            }
        }

        return flowNames;
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

            logger.info("Running flow {} with jobId {}", flowName, jobId);

            response = flowRunner.runFlow(inputs);
        } else {
            logger.warn("runFlow missing parameters, flowName: " + flowName + "steps: " + steps);
        }
        return response;
    }
}