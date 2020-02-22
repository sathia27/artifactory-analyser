package com.jrog.artifactoryanalyser.service;

import com.jfrog.artifactoryanalyser.model.request.ArtifactRequest;
import com.jfrog.artifactoryanalyser.service.ArtifactoryClient;
import com.jfrog.artifactoryanalyser.service.JfrogArtifactoryClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JfrogArtifactoryClientTest {

    @Autowired
    private ArtifactoryClient artifactoryClient;

    @MockBean
    private ExecutorService executorService;


    @Test
    public void fetchArtifactResponse(){
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setRepo("jcenter");
        artifactoryClient.listArtifacts(artifactRequest);
        Assertions.assertEquals(1, 1);
    }

    @BeforeEach
    public void setUp(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
    }
}
