package com.marklogic.dhf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.dhf.model.JobList;
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
public class JobListController {
    private final Logger logger = LoggerFactory.getLogger(JobListController.class);

    private final HubConfigService hubConfigService;
    private final String jobListJavascript;


    public JobListController(
            @Autowired HubConfigService hubConfigService,
            @Autowired ResourceLoader resourceLoader
    ) throws IOException {
        this.hubConfigService = hubConfigService;

        Resource jobListJavascript = resourceLoader.getResource("classpath:/javascript/jobList.sjs");
        try (InputStream is = jobListJavascript.getInputStream()) {
            this.jobListJavascript = StreamUtils.copyToString(is, Charsets.UTF_8);
        }
    }

    @GetMapping("/jobs")
    public String harmonize(@RequestParam(required = false, defaultValue = "1") Integer page,
                            @RequestParam(required = false, defaultValue = "50") Integer pageSize,
                            Principal principal,
                            Model model
    ) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        String username = authToken.getName();
        String password = authToken.getCredentials().toString();

        HubConfig hubConfig = this.hubConfigService.hubConfig(username, password);
        DatabaseClient jobClient = hubConfig.newJobDbClient();

        JobList jobList = null;
        try {
            JsonNode rootNode = jobClient.newServerEval()
                    .javascript(this.jobListJavascript)
                    .addVariable("page", page)
                    .addVariable("pageSize", pageSize)
                    .evalAs(JsonNode.class);

            jobList = new JobList().deserialize(rootNode);
        } catch (ResourceNotFoundException ex) {
        }

        model.addAttribute("jobList", jobList);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);

        if(jobList != null && jobList.getTotal() != null) {
            int total = jobList.getTotal();
            int pageCount = (int) Math.ceil((double)total / (double)pageSize);
            model.addAttribute("pageCount", pageCount);
        } else {
            model.addAttribute("pageCount", 0);
        }

        return "job-list";
    }
}
