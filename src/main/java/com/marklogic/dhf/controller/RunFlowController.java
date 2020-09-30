package com.marklogic.dhf.controller;

import com.marklogic.dhf.service.DataHubService;
import com.marklogic.hub.flow.RunFlowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//curl 'http://localhost:8090/harmonize?host=localhost&username=admin&password=admin&flowName=testFlow&steps=1,2'

/*
 * REST Service to launch a DHF harmonize flow
 * Example: curl 'http://localhost:8090/harmonize?username=admin&password=admin&flowName=testFlow&steps=1,2'
 */
@Controller
public class RunFlowController {
    private final Logger logger = LoggerFactory.getLogger(RunFlowController.class);

    private final DataHubService dhService;

    public RunFlowController(
            @Autowired DataHubService dhService
    ) {
        this.dhService = dhService;
    }

    @GetMapping("/runFlow")
    public String runFlow(@RequestParam String flowName,
                            @RequestParam(required = false) String steps,
                            Principal principal,
                            Model model
    ) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        String username = authToken.getName();
        String password = authToken.getCredentials().toString();

        String jobId = UUID.randomUUID().toString();

        List<String> wantedSteps = (steps != null) ? Arrays.asList(steps.split(",")) : null;
        RunFlowResponse response = dhService.runFlow(flowName, wantedSteps, jobId, username, password);

        model.addAttribute("response", response);
        return "run-flow";
    }

}