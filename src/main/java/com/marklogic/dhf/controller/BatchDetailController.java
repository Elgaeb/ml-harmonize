package com.marklogic.dhf.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.dhf.model.Batch;
import com.marklogic.dhf.service.HubConfigService;
import com.marklogic.hub.HubConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class BatchDetailController {
    private final Logger logger = LoggerFactory.getLogger(BatchDetailController.class);

    private final HubConfigService hubConfigService;

    public BatchDetailController(
            @Autowired HubConfigService hubConfigService
    ) {
        this.hubConfigService = hubConfigService;
    }

    @GetMapping("/batch")
    public String jobStatus(@RequestParam String batchId,
                            Principal principal,
                            Model model
    ) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        String username = authToken.getName();
        String password = authToken.getCredentials().toString();

        HubConfig hubConfig = this.hubConfigService.hubConfig(username, password);
        DatabaseClient jobClient = hubConfig.newJobDbClient();

        Batch batch = null;
        try {
            JsonNode jobDocument = jobClient.newJSONDocumentManager().read("/jobs/batches/" + batchId + ".json", new JacksonHandle()).get();
            JsonNode jobNode = jobDocument.get("batch");

            batch = new Batch().deserialize(jobNode);
        } catch (ResourceNotFoundException ex) {
        }

        model.addAttribute("batchId", batchId);
        model.addAttribute("batch", batch);
        return "batch-detail";
    }
}
