package com.salaboy.conferences.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.conferences.email.model.Proposal;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
@EnableZeebeClient
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${version:0.0.0}")
    private String version;

    @GetMapping("/info")
    public String infoWithVersion() {
        return "Email v" + version;
    }

    @PostMapping()
    public void sendEmailNotification(@RequestBody Proposal proposal) {
        System.out.println("Email Sent for proposal: " + proposal);
    }

    @ZeebeWorker(name = "email-worker", type = "email")
    public void sendEmailNotification(final JobClient client, final ActivatedJob job) {
        Proposal proposal = objectMapper.convertValue(job.getVariablesAsMap().get("proposal"), Proposal.class);
        sendEmailNotification(proposal);
        client.newCompleteCommand(job.getKey()).send();
    }


}
