package com.homework.velsera.cgccli.velsera.cgccli.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sevenbridges.apiclient.client.ApiKeys;
import com.sevenbridges.apiclient.client.AuthenticationScheme;
import com.sevenbridges.apiclient.client.Client;
import com.sevenbridges.apiclient.client.ClientBuilder;
import com.sevenbridges.apiclient.client.Clients;

import jakarta.annotation.PostConstruct;

@Component
public class SbClientService {

    private final Environment env;
    private Client sbClient;

    @Autowired
    public SbClientService(Environment environment){
        this.env = environment;
    }

    public Client getClient(){
        return sbClient;
    }

    @PostConstruct
    private void initClient(){
        ClientBuilder builder = Clients.builder();
        builder.setApiEndpoint(env.getProperty("SB_API_ENDPOINT"));
        builder.setAuthenticationScheme(AuthenticationScheme.AUTH_TOKEN);
        builder.setApiKey(ApiKeys.builder()
        .setSecret(env.getProperty("SB_AUTH_TOKEN"))
        .build());
        sbClient = builder.build();
    }

    public boolean isValidClient(){
        return sbClient != null 
            && env.getProperty("SB_AUTH_TOKEN") != null 
            && env.getProperty("SB_API_ENDPOINT") != null ;
    }

    public String clientValidityCheck(){
        return isValidClient() && sbClient.getCurrentUser() != null? 
            "CGC CLI is configured" : 
            "Check SB_AUTH_TOKEN and SB_API_ENDPOINT environment variables";
    }
}
