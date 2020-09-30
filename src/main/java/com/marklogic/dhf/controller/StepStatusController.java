package com.marklogic.dhf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.dhf.model.Job;
import com.marklogic.dhf.model.StepSummary;
import com.marklogic.dhf.service.HubConfigService;
import com.marklogic.hub.HubConfig;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

@Controller
public class StepStatusController {
    private final Logger logger = LoggerFactory.getLogger(StepStatusController.class);

    private final HubConfigService hubConfigService;
    private final String stepStatusJavascript;

    public StepStatusController(
            @Autowired HubConfigService hubConfigService,
            @Autowired ResourceLoader resourceLoader
    ) throws IOException {
        this.hubConfigService = hubConfigService;

        Resource jobListJavascript = resourceLoader.getResource("classpath:/javascript/stepStatus.sjs");
        try (InputStream is = jobListJavascript.getInputStream()) {
            this.stepStatusJavascript = StreamUtils.copyToString(is, Charsets.UTF_8);
        }

    }

    @GetMapping("/stepStatus")
    public String jobStatus(@RequestParam String jobId,
                            @RequestParam String stepNumber,
                            @RequestParam(required = false, defaultValue = "1") Integer page,
                            @RequestParam(required = false, defaultValue = "50") Integer pageSize,
                            Principal principal,
                            Model model
    ) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        String username = authToken.getName();
        String password = authToken.getCredentials().toString();

        HubConfig hubConfig = this.hubConfigService.hubConfig(username, password);
        DatabaseClient jobClient = hubConfig.newJobDbClient();

        StepSummary stepSummary = null;
        try {
            JsonNode rootNode = jobClient.newServerEval()
                    .javascript(this.stepStatusJavascript)
                    .addVariable("jobId", jobId)
                    .addVariable("stepNumber", stepNumber)
                    .addVariable("page", page)
                    .addVariable("pageSize", pageSize)
                    .evalAs(JsonNode.class);

            stepSummary = new StepSummary().deserialize(rootNode);
        } catch (ResourceNotFoundException ex) {
        }

        model.addAttribute("jobId", jobId);
        model.addAttribute("stepNumber", stepNumber);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("stepSummary", stepSummary);

        if(stepSummary != null && stepSummary.getTotal() != null) {
            int total = stepSummary.getTotal();
            int pageCount = (int) Math.ceil((double)total / (double)pageSize);
            model.addAttribute("pageCount", pageCount);
        } else {
            model.addAttribute("pageCount", 0);
        }

        return "step-summary";
    }
}
