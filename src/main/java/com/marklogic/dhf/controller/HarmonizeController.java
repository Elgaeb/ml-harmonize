package com.marklogic.dhf.controller;

import com.marklogic.dhf.model.FlowInput;
import com.marklogic.dhf.service.DataHubService;
import com.marklogic.dhf.service.HubConfigService;
import com.marklogic.hub.flow.RunFlowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class HarmonizeController {
    private final Logger logger = LoggerFactory.getLogger(HarmonizeController.class);

    private final HubConfigService hubConfigService;
    private final DataHubService dataHubService;

    public HarmonizeController(
            @Autowired HubConfigService hubConfigService,
            @Autowired DataHubService dataHubService,
            @Autowired ResourceLoader resourceLoader
    ) {
        this.hubConfigService = hubConfigService;
        this.dataHubService = dataHubService;
    }

    @GetMapping("/harmonize")
    public String harmonize(@ModelAttribute FlowInput flowInput,
                            Principal principal,
                            Model model
    ) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        String username = authToken.getName();
        String password = authToken.getCredentials().toString();

        model.addAttribute("allFlows", dataHubService.allFlows(username, password));

        return "harmonize";
    }

    @PostMapping("/harmonize")
    public String runFlow(@ModelAttribute FlowInput flowInput,
                          BindingResult bindingResult,
                          Principal principal,
                          Model model
    ) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        String username = authToken.getName();
        String password = authToken.getCredentials().toString();

        String jobId = UUID.randomUUID().toString();

        String flowName = flowInput.getName();
        if(flowName != null && flowName.trim() != "") {
            String steps = flowInput.getSteps();
            List<String> wantedSteps = (steps != null && steps.trim() != "") ? Arrays.asList(steps.split(",")) : null;
            RunFlowResponse response = this.dataHubService.runFlow(flowName.trim(), wantedSteps, jobId, username, password);
            return "redirect:/jobStatus?jobId=" + response.getJobId();
        }

        return "harmonize";
    }
}
